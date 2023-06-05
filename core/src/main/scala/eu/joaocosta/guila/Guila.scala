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

  def window(inputState: InputState, uiState: UiState)(
      run: (inputState: InputState, uiState: UiState) ?=> Unit
  ): (List[RenderOp], UiState) =
    val nextState = uiState.clone()
    // prepare
    nextState.ops.clear()
    nextState.hotItem = None
    // run
    given is: InputState = inputState
    given us: UiState    = nextState
    run
    // finish
    if (!inputState.mouseDown) nextState.activeItem = None
    // return
    (nextState.ops.toList, nextState)

  def rectangle(area: Rect, color: Color)(implicit uiState: UiState): Unit =
    uiState.ops.addOne(RenderOp.DrawRect(area, color))

  def text(area: Rect, text: String, color: Color, center: Boolean = false)(implicit uiState: UiState): Unit =
    if (text.nonEmpty) uiState.ops.addOne(RenderOp.DrawText(area, text, color, center))

  def button(id: ItemId, area: Rect, text: String = "", skin: ButtonSkin = ButtonSkin.Default())(implicit
      inputState: InputState,
      uiState: UiState
  ): Boolean =
    val buttonArea    = skin.buttonArea(area)
    val (hot, active) = setHotActive(id, buttonArea)
    skin.renderButton(area, text, hot, active)
    hot && active && inputState.mouseDown == false

  def checkbox(id: ItemId, area: Rect, skin: CheckboxSkin = CheckboxSkin.Default())(value: Boolean)(implicit
      inputState: InputState,
      uiState: UiState
  ): Boolean =
    val checkboxArea  = skin.checkboxArea(area)
    val (hot, active) = setHotActive(id, checkboxArea)
    skin.renderCheckbox(area, value, hot, active)
    if (hot && active && inputState.mouseDown == false) !value
    else value

  def slider(id: ItemId, area: Rect, skin: SliderSkin = SliderSkin.Default())(
      value: Int,
      max: Int
  )(implicit inputState: InputState, uiState: UiState): Int =
    val sliderArea    = skin.sliderArea(area)
    val sliderSize    = skin.sliderSize
    val (hot, active) = setHotActive(id, sliderArea)
    skin.renderSlider(area, value, max, hot, active)
    if (active)
      if (area.w > area.h)
        val mousePos = inputState.mouseX - sliderArea.x - sliderSize / 2
        val maxPos   = sliderArea.w - sliderSize
        math.max(0, math.min((mousePos * max) / maxPos, max))
      else
        val mousePos = inputState.mouseY - sliderArea.y - sliderSize / 2
        val maxPos   = sliderArea.h - sliderSize
        math.max(0, math.min((mousePos * max) / maxPos, max))
    else value
