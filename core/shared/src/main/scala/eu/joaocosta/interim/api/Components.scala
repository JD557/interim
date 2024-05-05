package eu.joaocosta.interim.api

import eu.joaocosta.interim._
import eu.joaocosta.interim.skins._

/** Object containing the default components.
  *
  * By convention, all components are functions in the form `def component(id, ...params, skin)(area, value): Value`.
  *
  * The area parameter can be ommited if there's an area allocator in scope.
  */
object Components extends Components

trait Components:

  type Component[+T] = (inputState: InputState.Historical, uiContext: UiContext) ?=> T

  trait ComponentWithValue[T]:
    def allocateArea(using allocator: LayoutAllocator.AreaAllocator): Rect

    def render(area: Rect, value: Ref[T]): Component[Unit]

    def render(value: Ref[T])(using allocator: LayoutAllocator.AreaAllocator): Component[Unit] =
      render(allocateArea, value)

    def applyRef(area: Rect, value: Ref[T]): Component[T] =
      render(area, value)
      value.get

    def applyValue(area: Rect, value: T): Component[T] =
      apply(area, Ref(value))

    inline def apply(area: Rect, value: T | Ref[T]): Component[T] = inline value match
      case x: T      => applyValue(area, x)
      case x: Ref[T] => applyRef(area, x)

    inline def apply(value: T | Ref[T])(using allocator: LayoutAllocator.AreaAllocator): Component[T] =
      apply(allocateArea, value)

  trait ComponentWithBody[I, F[_]]:
    def allocateArea(using allocator: LayoutAllocator.AreaAllocator): Rect

    def render[T](area: Rect, body: I => T): Component[F[T]]

    def render[T](body: I => T)(using allocator: LayoutAllocator.AreaAllocator): Component[Unit] =
      render(allocateArea, body)

    def apply[T](area: Rect)(body: I => T): Component[F[T]] = render(area, body)

    def apply[T](area: Rect)(body: => T)(using ev: I =:= Unit): Component[F[T]] = render(area, _ => body)

    def apply[T](body: I => T)(using allocator: LayoutAllocator.AreaAllocator): Component[F[T]] =
      render(allocateArea, body)

    def apply[T](body: => T)(using allocator: LayoutAllocator.AreaAllocator, ev: I =:= Unit): Component[F[T]] =
      render(allocateArea, _ => body)

  /** Button component. Returns true if it's being clicked, false otherwise.
    *
    * @param label text label to show on this button
    */
  final def button(
      id: ItemId,
      label: String,
      skin: ButtonSkin = ButtonSkin.default()
  ): ComponentWithBody[Unit, Option] =
    new ComponentWithBody[Unit, Option]:
      def allocateArea(using allocator: LayoutAllocator.AreaAllocator): Rect =
        skin.allocateArea(allocator, label)

      def render[T](area: Rect, body: Unit => T): Component[Option[T]] =
        val buttonArea = skin.buttonArea(area)
        val itemStatus = UiContext.registerItem(id, buttonArea)
        skin.renderButton(area, label, itemStatus)
        Option.when(itemStatus.clicked)(body(()))

  /** Checkbox component. Returns true if it's enabled, false otherwise.
    */
  final def checkbox(
      id: ItemId,
      skin: CheckboxSkin = CheckboxSkin.default()
  ): ComponentWithValue[Boolean] =
    new ComponentWithValue[Boolean]:
      def allocateArea(using allocator: LayoutAllocator.AreaAllocator): Rect =
        skin.allocateArea(allocator)

      def render(area: Rect, value: Ref[Boolean]): Component[Unit] =
        val checkboxArea = skin.checkboxArea(area)
        val itemStatus   = UiContext.registerItem(id, checkboxArea)
        skin.renderCheckbox(area, value.get, itemStatus)
        value.modifyIf(itemStatus.clicked)(!_)

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
      def allocateArea(using allocator: LayoutAllocator.AreaAllocator): Rect =
        skin.allocateArea(allocator, label)

      def render(area: Rect, value: Ref[T]): Component[Unit] =
        val buttonArea = skin.buttonArea(area)
        val itemStatus = UiContext.registerItem(id, buttonArea)
        if (itemStatus.clicked) value := buttonValue
        if (value.get == buttonValue) skin.renderButton(area, label, itemStatus.copy(hot = true, active = true))
        else skin.renderButton(area, label, itemStatus)

  /** Select box component. Returns the index value currently selected inside a PanelState.
    *
    * It also allows one to define a default label if the value is invalid.
    * This can be particularly to keep track if a user has selected no value, by setting the
    * initial value to an invalid sentinel value (e.g. -1).
    *
    * @param labels text labels for each value
    * @param defaultLabel label to print if the value doesn't match any of the labels.
    */
  final def select(
      id: ItemId,
      labels: Vector[String],
      defaultLabel: String = "",
      skin: SelectSkin = SelectSkin.default()
  ): ComponentWithValue[PanelState[Int]] =
    new ComponentWithValue[PanelState[Int]]:
      def allocateArea(using allocator: LayoutAllocator.AreaAllocator): Rect =
        skin.allocateArea(allocator, labels)

      def render(area: Rect, value: Ref[PanelState[Int]]): Component[Unit] =
        val selectBoxArea = skin.selectBoxArea(area)
        val itemStatus    = UiContext.registerItem(id, area)
        value.modifyIf(itemStatus.selected)(_.open)
        skin.renderSelectBox(area, value.get.value, labels, defaultLabel, itemStatus)
        if (value.get.isOpen)
          value.modifyIf(!itemStatus.selected)(_.close)
          Primitives.onTop:
            labels.zipWithIndex
              .foreach: (label, idx) =>
                val selectOptionArea = skin.selectOptionArea(area, idx)
                val optionStatus     = UiContext.registerItem(id |> idx, selectOptionArea)
                skin.renderSelectOption(area, idx, labels, optionStatus)
                if (optionStatus.active) value := PanelState.closed(idx)

  /** Slider component. Returns the current position of the slider, between min and max.
    *
    * @param min minimum value for this slider
    * @param max maximum value fr this slider
    */
  final def slider(
      id: ItemId,
      min: Int,
      max: Int,
      skin: SliderSkin = SliderSkin.default()
  ): ComponentWithValue[Int] =
    new ComponentWithValue[Int]:
      def allocateArea(using allocator: LayoutAllocator.AreaAllocator): Rect =
        skin.allocateArea(allocator)

      def render(area: Rect, value: Ref[Int]): Component[Unit] =
        val sliderArea   = skin.sliderArea(area)
        val steps        = max - min + 1
        val itemStatus   = UiContext.registerItem(id, sliderArea)
        val clampedValue = math.max(min, math.min(value.get, max))
        skin.renderSlider(area, min, clampedValue, max, itemStatus)
        if (itemStatus.active)
          summon[InputState].mouseInput.position.foreach: (mouseX, mouseY) =>
            val intPosition =
              if (area.w > area.h) steps * (mouseX - sliderArea.x) / sliderArea.w
              else steps * (mouseY - sliderArea.y) / sliderArea.h
            value := math.max(min, math.min(min + intPosition, max))

  /** Text input component. Returns the current string inputed.
    */
  final def textInput(
      id: ItemId,
      skin: TextInputSkin = TextInputSkin.default()
  ): ComponentWithValue[String] =
    new ComponentWithValue[String]:
      def allocateArea(using allocator: LayoutAllocator.AreaAllocator): Rect =
        skin.allocateArea(allocator)

      def render(area: Rect, value: Ref[String]): Component[Unit] =
        val textInputArea = skin.textInputArea(area)
        val itemStatus    = UiContext.registerItem(id, textInputArea)
        skin.renderTextInput(area, value.get, itemStatus)
        value.modifyIf(itemStatus.selected)(summon[InputState].appendKeyboardInput)

  /** Draggable handle. Returns the moved area.
    *
    * Instead of using this component directly, it can be easier to use [[eu.joaocosta.interim.api.Panels.window]]
    * with movable = true.
    */
  final def moveHandle(id: ItemId, skin: HandleSkin = HandleSkin.default()): ComponentWithValue[Rect] =
    new ComponentWithValue[Rect]:
      def allocateArea(using allocator: LayoutAllocator.AreaAllocator): Rect =
        skin.allocateArea(allocator)

      def render(area: Rect, value: Ref[Rect]): Component[Unit] =
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
  final def resizeHandle(id: ItemId, skin: HandleSkin = HandleSkin.default()): ComponentWithValue[Rect] =
    new ComponentWithValue[Rect]:
      def allocateArea(using allocator: LayoutAllocator.AreaAllocator): Rect =
        skin.allocateArea(allocator)

      def render(area: Rect, value: Ref[Rect]): Component[Unit] =
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
      skin: HandleSkin = HandleSkin.default()
  ): ComponentWithValue[PanelState[T]] =
    new ComponentWithValue[PanelState[T]]:
      def allocateArea(using allocator: LayoutAllocator.AreaAllocator): Rect =
        skin.allocateArea(allocator)

      def render(area: Rect, value: Ref[PanelState[T]]): Component[Unit] =
        val handleArea = skin.closeHandleArea(area)
        val itemStatus = UiContext.registerItem(id, handleArea)
        skin.renderCloseHandle(area, itemStatus)
        value.modifyIf(itemStatus.clicked)(_.close)
