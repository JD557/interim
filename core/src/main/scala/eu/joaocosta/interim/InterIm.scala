package eu.joaocosta.interim

import eu.joaocosta.interim.skins._

object InterIm:
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

  def grid[T](area: Rect, numRows: Int, numColumns: Int, padding: Int)(body: Vector[Vector[Rect]] => T): T =
    body(rows(area, numRows, padding)(_.map(subArea => columns(subArea, numColumns, padding)(identity))))

  def rows[T](area: Rect, numRows: Int, padding: Int)(body: Vector[Rect] => T): T =
    val rowSize = (area.h - (numRows - 1) * padding) / numRows
    val vec = for
      row <- (0 until numRows)
      dy = row * (rowSize + padding)
    yield Rect(area.x, area.y + dy, area.w, rowSize)
    body(vec.toVector)

  def columns[T](area: Rect, numColumns: Int, padding: Int)(body: Vector[Rect] => T): T =
    val columnSize = (area.w - (numColumns - 1) * padding) / numColumns
    val vec = for
      column <- (0 until numColumns)
      dx = column * (columnSize + padding)
    yield Rect(area.x + dx, area.y, columnSize, area.h)
    body(vec.toVector)

  def rectangle(area: Rect, color: Color)(implicit uiState: UiState): Unit =
    uiState.ops.addOne(RenderOp.DrawRect(area, color))

  def text(area: Rect, text: String, fontSize: Int, color: Color, center: Boolean = false)(implicit
      uiState: UiState
  ): Unit =
    if (text.nonEmpty) uiState.ops.addOne(RenderOp.DrawText(area, text, fontSize, color, center))

  def button(
      id: ItemId,
      area: Rect,
      label: String = "",
      fontSize: Int = 8,
      skin: ButtonSkin = ButtonSkin.Default()
  ): Component[Boolean] =
    val buttonArea    = skin.buttonArea(area)
    val (hot, active) = setHotActive(id, buttonArea)
    skin.renderButton(area, label, fontSize, hot, active)
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
      min: Int,
      value: Int,
      max: Int
  ): Component[Int] =
    val sliderArea    = skin.sliderArea(area)
    val sliderSize    = skin.sliderSize
    val range         = max - min
    val (hot, active) = setHotActive(id, sliderArea)
    val clampedValue  = math.max(min, math.min(value, max))
    skin.renderSlider(area, min, clampedValue, max, hot, active)
    if (active)
      if (area.w > area.h)
        val mousePos = summon[InputState].mouseX - sliderArea.x - sliderSize / 2
        val maxPos   = sliderArea.w - sliderSize
        math.max(min, math.min(min + (mousePos * range) / maxPos, max))
      else
        val mousePos = summon[InputState].mouseY - sliderArea.y - sliderSize / 2
        val maxPos   = sliderArea.h - sliderSize
        math.max(min, math.min((mousePos * range) / maxPos, max))
    else value
