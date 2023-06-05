package eu.joaocosta.guila

import eu.joaocosta.guila.skins._

object Guila:
  private def setHotActive(id: ItemId, area: Rect)(implicit
      inputState: InputState,
      uiState: UiState
  ): (Boolean, Boolean) =
    if (area.isMouseOver)
      uiState.hotItem = Some(id)
      if (uiState.activeItem == None && inputState.mouseDown)
        uiState.activeItem = Some(id)
    (uiState.hotItem == Some(id), uiState.activeItem == Some(id))

  type Component[+T] = (inputState: InputState, uiState: UiState) ?=> T

  def window[T](inputState: InputState, uiState: UiState)(
      run: (inputState: InputState, uiState: UiState) ?=> T
  ): (List[RenderOp], UiState, T) =
    val nextState = uiState.clone()
    // prepare
    nextState.ops.clear()
    nextState.hotItem = None
    // run
    given is: InputState = inputState
    given us: UiState    = nextState
    val res              = run
    // finish
    if (!inputState.mouseDown) nextState.activeItem = None
    // return
    (nextState.ops.toList, nextState, res)

  def grid[T](area: Rect, rows: Int, columns: Int, padding: Int)(body: Iterator[Rect] => T): T =
    val rowSize    = (area.h - (rows - 1) * padding) / rows
    val columnSize = (area.w - (columns - 1) * padding) / columns
    val it = for
      row <- (0 until rows).iterator
      dy = row * (rowSize + padding)
      column <- (0 until columns).iterator
      dx = column * (columnSize + padding)
    yield Rect(area.x + dx, area.y + dy, columnSize, rowSize)
    body(it)

  def rectangle(area: Rect, color: Color)(implicit uiState: UiState): Unit =
    uiState.ops.addOne(RenderOp.DrawRect(area, color))

  def text(area: Rect, text: String, color: Color, center: Boolean = false)(implicit uiState: UiState): Unit =
    if (text.nonEmpty) uiState.ops.addOne(RenderOp.DrawText(area, text, color, center))

  def button(id: ItemId, area: Rect, label: String = "", skin: ButtonSkin = ButtonSkin.Default()): Component[Boolean] =
    val buttonArea    = skin.buttonArea(area)
    val (hot, active) = setHotActive(id, buttonArea)
    skin.renderButton(area, label, hot, active)
    hot && active && summon[InputState].mouseDown == false

  def checkbox(id: ItemId, area: Rect, skin: CheckboxSkin = CheckboxSkin.Default())(
      value: Boolean
  ): Component[Boolean] =
    val checkboxArea  = skin.checkboxArea(area)
    val (hot, active) = setHotActive(id, checkboxArea)
    skin.renderCheckbox(area, value, hot, active)
    if (hot && active && summon[InputState].mouseDown == false) !value
    else value

  def slider(id: ItemId, area: Rect, skin: SliderSkin = SliderSkin.Default())(
      value: Int,
      max: Int
  ): Component[Int] =
    val sliderArea    = skin.sliderArea(area)
    val sliderSize    = skin.sliderSize
    val (hot, active) = setHotActive(id, sliderArea)
    skin.renderSlider(area, value, max, hot, active)
    if (active)
      if (area.w > area.h)
        val mousePos = summon[InputState].mouseX - sliderArea.x - sliderSize / 2
        val maxPos   = sliderArea.w - sliderSize
        math.max(0, math.min((mousePos * max) / maxPos, max))
      else
        val mousePos = summon[InputState].mouseY - sliderArea.y - sliderSize / 2
        val maxPos   = sliderArea.h - sliderSize
        math.max(0, math.min((mousePos * max) / maxPos, max))
    else value
