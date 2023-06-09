package eu.joaocosta.interim.api

import eu.joaocosta.interim.*
import eu.joaocosta.interim.skins.*

/** Object containing the default components.
  *
  * By convention, all components are functions in the form `def component(id, area, ...params, skin)(value): Value`.
  */
object Components extends Components

trait Components:

  type Component[+T] = (inputState: InputState, uiState: UiState) ?=> T

  /** Button component. Returns true if it's being clicked, false otherwise.
    *
    * @param label optional text label (and font size in px) to show on this button
    */
  final def button(
      id: ItemId,
      area: Rect,
      label: Option[(String, Int)] = None,
      skin: ButtonSkin = ButtonSkin.Default()
  ): Component[Boolean] =
    val buttonArea = skin.buttonArea(area)
    val itemStatus = UiState.registerItem(id, buttonArea)
    skin.renderButton(area, label, itemStatus)
    itemStatus.hot && itemStatus.active && summon[InputState].mouseDown == false

  /** Checkbox component. Returns true if it's enabled, false otherwise.
    */
  final def checkbox(id: ItemId, area: Rect, skin: CheckboxSkin = CheckboxSkin.Default())(
      value: Boolean
  ): Component[Boolean] =
    val checkboxArea = skin.checkboxArea(area)
    val itemStatus   = UiState.registerItem(id, checkboxArea)
    skin.renderCheckbox(area, value, itemStatus)
    if (itemStatus.hot && itemStatus.active && summon[InputState].mouseDown == false) !value
    else value

  /** Slider component. Returns the current position of the slider, between min and max.
    *
    * @param min minimum value for this slider
    * @param max maximum value fr this slider
    */
  final def slider(id: ItemId, area: Rect, min: Int, max: Int, skin: SliderSkin = SliderSkin.Default())(
      value: Int
  ): Component[Int] =
    val sliderArea   = skin.sliderArea(area)
    val sliderSize   = skin.sliderSize
    val range        = max - min
    val itemStatus   = UiState.registerItem(id, sliderArea)
    val clampedValue = math.max(min, math.min(value, max))
    skin.renderSlider(area, min, clampedValue, max, itemStatus)
    if (itemStatus.active)
      if (area.w > area.h)
        val mousePos = summon[InputState].mouseX - sliderArea.x - sliderSize / 2
        val maxPos   = sliderArea.w - sliderSize
        math.max(min, math.min(min + (mousePos * range) / maxPos, max))
      else
        val mousePos = summon[InputState].mouseY - sliderArea.y - sliderSize / 2
        val maxPos   = sliderArea.h - sliderSize
        math.max(min, math.min((mousePos * range) / maxPos, max))
    else value

  /** Text input component. Returns the current string inputed.
    */
  final def textInput(id: ItemId, area: Rect, skin: TextInputSkin = TextInputSkin.Default())(
      value: String
  ): Component[String] =
    val textInputArea = skin.textInputArea(area)
    val itemStatus    = UiState.registerItem(id, textInputArea)
    skin.renderTextInput(area, value, itemStatus)
    if (itemStatus.keyboardFocus) summon[InputState].appendKeyboardInput(value)
    else value

  /** Draggable handle. Returns the moved area.
    *
    *  It's important that this element moves along with the moved area.
    */
  final def moveHandle(id: ItemId, area: Rect, skin: HandleSkin = HandleSkin.Default())(
      value: Rect
  ): Component[Rect] =
    val handleArea = skin.handleArea(area)
    val itemStatus = UiState.registerItem(id, handleArea)
    skin.renderHandle(area, value, itemStatus)
    if (itemStatus.active)
      val handleCenterX = handleArea.x + handleArea.w / 2
      val handleCenterY = handleArea.y + handleArea.h / 2
      val mouseX        = summon[InputState].mouseX
      val mouseY        = summon[InputState].mouseY
      val deltaX        = mouseX - handleCenterX
      val deltaY        = mouseY - handleCenterY
      value.move(deltaX, deltaY)
    else value
