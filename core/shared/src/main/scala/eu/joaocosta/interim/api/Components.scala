package eu.joaocosta.interim.api

import eu.joaocosta.interim._
import eu.joaocosta.interim.skins._

/** Object containing the default components.
  *
  * By convention, all components are functions in the form `def component(id, area, ...params, skin)(value): Value`.
  */
object Components extends Components

trait Components:

  type Component[+T] = (inputState: InputState.Historical, uiContext: UiContext) ?=> T

  trait ComponentWithValue[T]:
    def render(value: Ref[T]): Component[Unit]

    def applyRef(value: Ref[T]): Component[T] =
      render(value)
      value.get

    def applyValue(value: T): Component[T] =
      apply(Ref(value))

    inline def apply(value: T | Ref[T]): Component[T] = inline value match
      case x: T      => applyValue(x)
      case x: Ref[T] => applyRef(x)

  trait ComponentWithBody[I, F[_]]:
    def render[T](body: I => T): Component[F[T]]

    def apply[T](body: I => T): Component[F[T]] = render(body)

    def apply[T](body: => T)(using ev: I =:= Unit): Component[F[T]] = render(_ => body)

  /** Button component. Returns true if it's being clicked, false otherwise.
    *
    * @param label text label to show on this button
    */
  final def button(
      id: ItemId,
      area: Rect | LayoutAllocator,
      label: String,
      skin: ButtonSkin = ButtonSkin.default()
  ): ComponentWithBody[Unit, Option] =
    new ComponentWithBody[Unit, Option]:
      def render[T](body: Unit => T): Component[Option[T]] =
        val reservedArea = area match {
          case rect: Rect             => rect
          case alloc: LayoutAllocator => skin.allocateArea(alloc, label)
        }
        val buttonArea = skin.buttonArea(reservedArea)
        val itemStatus = UiContext.registerItem(id, buttonArea)
        skin.renderButton(reservedArea, label, itemStatus)
        Option.when(itemStatus.clicked)(body(()))

  /** Checkbox component. Returns true if it's enabled, false otherwise.
    */
  final def checkbox(
      id: ItemId,
      area: Rect | LayoutAllocator,
      skin: CheckboxSkin = CheckboxSkin.default()
  ): ComponentWithValue[Boolean] =
    new ComponentWithValue[Boolean]:
      def render(value: Ref[Boolean]): Component[Unit] =
        val reservedArea = area match {
          case rect: Rect             => rect
          case alloc: LayoutAllocator => skin.allocateArea(alloc)
        }
        val checkboxArea = skin.checkboxArea(reservedArea)
        val itemStatus   = UiContext.registerItem(id, checkboxArea)
        skin.renderCheckbox(reservedArea, value.get, itemStatus)
        value.modifyIf(itemStatus.clicked)(!_)

  /** Radio button component. Returns value currently selected.
    *
    * @param buttonValue the value of this button (value that this button returns when selected)
    * @param label text label to show on this button
    */
  final def radioButton[T](
      id: ItemId,
      area: Rect | LayoutAllocator,
      buttonValue: T,
      label: String,
      skin: ButtonSkin = ButtonSkin.default()
  ): ComponentWithValue[T] =
    new ComponentWithValue[T]:
      def render(value: Ref[T]): Component[Unit] =
        val reservedArea = area match {
          case rect: Rect             => rect
          case alloc: LayoutAllocator => skin.allocateArea(alloc, label)
        }
        val buttonArea = skin.buttonArea(reservedArea)
        val itemStatus = UiContext.registerItem(id, buttonArea)
        if (itemStatus.clicked) value := buttonValue
        if (value.get == buttonValue) skin.renderButton(reservedArea, label, itemStatus.copy(hot = true, active = true))
        else skin.renderButton(reservedArea, label, itemStatus)

  /** Select box component. Returns the index value currently selected inside a PanelState.
    *
    * @param labels text labels for each value
    * @param undefinedFirstValue if true, the value 0 will not show on the options, acting as a default undefined value
    */
  final def select(
      id: ItemId,
      area: Rect | LayoutAllocator,
      labels: Vector[String],
      undefinedFirstValue: Boolean = false,
      skin: SelectSkin = SelectSkin.default()
  ): ComponentWithValue[PanelState[Int]] =
    new ComponentWithValue[PanelState[Int]]:
      def render(value: Ref[PanelState[Int]]): Component[Unit] =
        val reservedArea = area match {
          case rect: Rect             => rect
          case alloc: LayoutAllocator => skin.allocateArea(alloc, labels)
        }
        val selectBoxArea = skin.selectBoxArea(reservedArea)
        val itemStatus    = UiContext.registerItem(id, reservedArea)
        value.modifyIf(itemStatus.selected)(_.open)
        skin.renderSelectBox(reservedArea, value.get.value, labels, itemStatus)
        if (value.get.isOpen)
          value.modifyIf(!itemStatus.selected)(_.close)
          val selectableLabels = labels.drop(if (undefinedFirstValue) 1 else 0)
          Primitives.onTop:
            selectableLabels.zipWithIndex
              .foreach: (label, idx) =>
                val selectOptionArea = skin.selectOptionArea(reservedArea, idx)
                val optionStatus     = UiContext.registerItem(id |> idx, selectOptionArea)
                skin.renderSelectOption(reservedArea, idx, selectableLabels, optionStatus)
                if (optionStatus.active) value := PanelState.closed(if (undefinedFirstValue) idx + 1 else idx)

  /** Slider component. Returns the current position of the slider, between min and max.
    *
    * @param min minimum value for this slider
    * @param max maximum value fr this slider
    */
  final def slider(
      id: ItemId,
      area: Rect | LayoutAllocator,
      min: Int,
      max: Int,
      skin: SliderSkin = SliderSkin.default()
  ): ComponentWithValue[Int] =
    new ComponentWithValue[Int]:
      def render(value: Ref[Int]): Component[Unit] =
        val reservedArea = area match {
          case rect: Rect             => rect
          case alloc: LayoutAllocator => skin.allocateArea(alloc)
        }
        val sliderArea   = skin.sliderArea(reservedArea)
        val steps        = max - min + 1
        val itemStatus   = UiContext.registerItem(id, sliderArea)
        val clampedValue = math.max(min, math.min(value.get, max))
        skin.renderSlider(reservedArea, min, clampedValue, max, itemStatus)
        if (itemStatus.active)
          summon[InputState].mouseInput.position.foreach: (mouseX, mouseY) =>
            val intPosition =
              if (reservedArea.w > reservedArea.h) steps * (mouseX - sliderArea.x) / sliderArea.w
              else steps * (mouseY - sliderArea.y) / sliderArea.h
            value := math.max(min, math.min(min + intPosition, max))

  /** Text input component. Returns the current string inputed.
    */
  final def textInput(
      id: ItemId,
      area: Rect | LayoutAllocator,
      skin: TextInputSkin = TextInputSkin.default()
  ): ComponentWithValue[String] =
    new ComponentWithValue[String]:
      def render(value: Ref[String]): Component[Unit] =
        val reservedArea = area match {
          case rect: Rect             => rect
          case alloc: LayoutAllocator => skin.allocateArea(alloc)
        }
        val textInputArea = skin.textInputArea(reservedArea)
        val itemStatus    = UiContext.registerItem(id, textInputArea)
        skin.renderTextInput(reservedArea, value.get, itemStatus)
        value.modifyIf(itemStatus.selected)(summon[InputState].appendKeyboardInput)

  /** Draggable handle. Returns the moved area.
    *
    * Instead of using this component directly, it can be easier to use [[eu.joaocosta.interim.api.Panels.window]]
    * with movable = true.
    */
  final def moveHandle(id: ItemId, area: Rect, skin: HandleSkin = HandleSkin.default()): ComponentWithValue[Rect] =
    new ComponentWithValue[Rect]:
      def render(value: Ref[Rect]): Component[Unit] =
        val handleArea = skin.moveHandleArea(area)
        val itemStatus = UiContext.registerItem(id, handleArea)
        val deltaX     = summon[InputState.Historical].deltaX
        val deltaY     = summon[InputState.Historical].deltaY
        skin.renderMoveHandle(area, itemStatus)
        value.modifyIf(itemStatus.active)(_.move(deltaX, deltaY))

  /** Draggable handle. Returns the resized area.
    *
    * Instead of using this component directly, it can be easier to use [[eu.joaocosta.interim.api.Panels.window]]
    * with movable = true.
    */
  final def resizeHandle(id: ItemId, area: Rect, skin: HandleSkin = HandleSkin.default()): ComponentWithValue[Rect] =
    new ComponentWithValue[Rect]:
      def render(value: Ref[Rect]): Component[Unit] =
        val handleArea = skin.resizeHandleArea(area)
        val itemStatus = UiContext.registerItem(id, handleArea)
        val deltaX     = summon[InputState.Historical].deltaX
        val deltaY     = summon[InputState.Historical].deltaY
        skin.renderResizeHandle(area, itemStatus)
        value.modifyIf(itemStatus.active)(_.resize(deltaX, deltaY))

  /** Close handle. Closes the panel when clicked.
    *
    * Instead of using this component directly, it can be easier to use [[eu.joaocosta.interim.api.Panels.window]]
    * with closable = true.
    */
  final def closeHandle[T](
      id: ItemId,
      area: Rect,
      skin: HandleSkin = HandleSkin.default()
  ): ComponentWithValue[PanelState[T]] =
    new ComponentWithValue[PanelState[T]]:
      def render(value: Ref[PanelState[T]]): Component[Unit] =
        val handleArea = skin.closeHandleArea(area)
        val itemStatus = UiContext.registerItem(id, handleArea)
        skin.renderCloseHandle(area, itemStatus)
        value.modifyIf(itemStatus.clicked)(_.close)
