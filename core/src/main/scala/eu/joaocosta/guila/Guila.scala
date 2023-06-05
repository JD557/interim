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
    uiState.ops.addOne(RenderOp.DrawRect(area: Rect, color))

  def button(id: ItemId, area: Rect, skin: ButtonSkin = ButtonSkin.Default())(implicit
      inputState: InputState,
      uiState: UiState
  ): Boolean =
    val (hot, active) = setHotActive(id, area)
    skin.renderButton(area, hot, active)
    hot && active && inputState.mouseDown == false

  def slider(id: ItemId, area: Rect, skin: SliderSkin = SliderSkin.Default())(
      value: Int,
      max: Int
  )(implicit inputState: InputState, uiState: UiState): Int =
    val padding       = 8 // FIXME
    val smallSide     = math.min(area.w, area.h)
    val largeSide     = math.max(area.w, area.h)
    val sliderSize    = smallSide - 2 * padding
    val maxPos        = largeSide - 2 * padding - sliderSize
    val pos           = (maxPos * value) / max
    val (hot, active) = setHotActive(id, area)
    skin.renderSlider(area, value, max, hot, active)
    if (active)
      val mouseAbsPos =
        (if (area.w > area.h) inputState.mouseX - area.x else inputState.mouseY - area.y) - padding - sliderSize / 2
      val mousePos = math.max(0, math.min(mouseAbsPos, maxPos))
      val newValue = (mousePos * max) / maxPos
      newValue
    else value
