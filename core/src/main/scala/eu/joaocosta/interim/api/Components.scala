package eu.joaocosta.interim.api

import eu.joaocosta.interim.*
import eu.joaocosta.interim.skins.*

/** Object containing the default components.
  *
  * By convention, all components are functions in the form `def component(id, area, ...params, skin)(value): Value`.
  */
object Components extends Components

trait Components:

  type Component[+T] = (inputState: InputState, uiContext: UiContext) ?=> T

  trait ComponentWithValue[T] {
    def applyRef(value: Ref[T]): Component[T]
    def applyValue(value: T): Component[T] =
      apply(Ref(value))
    inline def apply(value: T | Ref[T]): Component[T] = inline value match
      case x: T      => applyValue(x)
      case x: Ref[T] => applyRef(x)
  }

  /** Button component. Returns true if it's being clicked, false otherwise.
    *
    * @param label text label to show on this button
    */
  final def button(
      id: ItemId,
      area: Rect,
      label: String,
      skin: ButtonSkin = ButtonSkin.default()
  ): Component[Boolean] =
    val buttonArea = skin.buttonArea(area)
    val itemStatus = UiContext.registerItem(id, buttonArea)
    skin.renderButton(area, label, itemStatus)
    itemStatus.hot && itemStatus.active && summon[InputState].mouseDown == false

  /** Checkbox component. Returns true if it's enabled, false otherwise.
    */
  final def checkbox(id: ItemId, area: Rect, skin: CheckboxSkin = CheckboxSkin.default()): ComponentWithValue[Boolean] =
    new ComponentWithValue[Boolean]:
      def applyRef(value: Ref[Boolean]): Component[Boolean] =
        val checkboxArea = skin.checkboxArea(area)
        val itemStatus   = UiContext.registerItem(id, checkboxArea)
        skin.renderCheckbox(area, value.get, itemStatus)
        if (itemStatus.hot && itemStatus.active && summon[InputState].mouseDown == false)
          value.modify(!_).get
        else value.get

  /** Radio button component. Returns value currently selected.
    *
    * @param buttonValue the value of this button (value that this button returns when selected)
    * @param label text label to show on this button
    */
  final def radioButton[T](
      id: ItemId,
      area: Rect,
      buttonValue: T,
      label: String,
      skin: ButtonSkin = ButtonSkin.default()
  ): ComponentWithValue[T] =
    new ComponentWithValue[T]:
      def applyRef(value: Ref[T]): Component[T] =
        val buttonArea = skin.buttonArea(area)
        val itemStatus = UiContext.registerItem(id, buttonArea)
        if (itemStatus.hot && itemStatus.active && summon[InputState].mouseDown == false)
          value := buttonValue
        if (value.get == buttonValue) skin.renderButton(area, label, itemStatus.copy(hot = true, active = true))
        else (skin.renderButton(area, label, itemStatus))
        value.get

  /** Slider component. Returns the current position of the slider, between min and max.
    *
    * @param min minimum value for this slider
    * @param max maximum value fr this slider
    */
  final def slider(
      id: ItemId,
      area: Rect,
      min: Int,
      max: Int,
      skin: SliderSkin = SliderSkin.default()
  ): ComponentWithValue[Int] =
    new ComponentWithValue[Int]:
      def applyRef(value: Ref[Int]): Component[Int] =
        val sliderArea   = skin.sliderArea(area)
        val sliderSize   = skin.sliderSize(area, min, max)
        val range        = max - min
        val itemStatus   = UiContext.registerItem(id, sliderArea)
        val clampedValue = math.max(min, math.min(value.get, max))
        skin.renderSlider(area, min, clampedValue, max, itemStatus)
        if (itemStatus.active)
          if (area.w > area.h)
            val mousePos = summon[InputState].mouseX - sliderArea.x - sliderSize / 2
            val maxPos   = sliderArea.w - sliderSize
            value := math.max(min, math.min(min + (mousePos * range) / maxPos, max))
          else
            val mousePos = summon[InputState].mouseY - sliderArea.y - sliderSize / 2
            val maxPos   = sliderArea.h - sliderSize
            value := math.max(min, math.min((mousePos * range) / maxPos, max))
        value.get

  /** Text input component. Returns the current string inputed.
    */
  final def textInput(
      id: ItemId,
      area: Rect,
      skin: TextInputSkin = TextInputSkin.default()
  ): ComponentWithValue[String] =
    new ComponentWithValue[String]:
      def applyRef(value: Ref[String]): Component[String] =
        val textInputArea = skin.textInputArea(area)
        val itemStatus    = UiContext.registerItem(id, textInputArea)
        skin.renderTextInput(area, value.get, itemStatus)
        if (itemStatus.keyboardFocus)
          value.modify(summon[InputState].appendKeyboardInput)
        value.get

  /** Draggable handle. Returns the moved area.
    *
    * It's important that this element moves along with the moved area.
    *
    * Instead of using this component directly, it can be easier to use [[eu.joaocosta.interim.api.Panels.window]]
    * with movable = true.
    */
  final def moveHandle(id: ItemId, area: Rect, skin: HandleSkin = HandleSkin.default()): ComponentWithValue[Rect] =
    new ComponentWithValue[Rect]:
      def applyRef(value: Ref[Rect]): Component[Rect] =
        val handleArea = skin.handleArea(area)
        val itemStatus = UiContext.registerItem(id, handleArea)
        skin.renderHandle(area, value.get, itemStatus)
        if (itemStatus.active)
          val handleCenterX = handleArea.x + handleArea.w / 2
          val handleCenterY = handleArea.y + handleArea.h / 2
          val mouseX        = summon[InputState].mouseX
          val mouseY        = summon[InputState].mouseY
          val deltaX        = mouseX - handleCenterX
          val deltaY        = mouseY - handleCenterY
          value.modify(_.move(deltaX, deltaY))
        value.get
