package eu.joaocosta.guila

import eu.joaocosta.guila.skins._

object Guila:
  private def regionHit(x: Int, y: Int, w: Int, h: Int)(implicit inputState: InputState): Boolean =
    !(inputState.mouseX < x || inputState.mouseY < y || inputState.mouseX >= x + w || inputState.mouseY >= y + h)

  private def setHotActive(id: ItemId, x: Int, y: Int, w: Int, h: Int)(implicit
      inputState: InputState,
      uiState: UiState
  ): (Boolean, Boolean) =
    if (regionHit(x, y, w, h))
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

  def rectangle(x: Int, y: Int, w: Int, h: Int, color: Color)(implicit uiState: UiState): Unit =
    uiState.ops.addOne(RenderOp.DrawRect(x, y, w, h, color))

  def button(id: ItemId, x: Int, y: Int, w: Int = 64, h: Int = 48, skin: ButtonSkin = ButtonSkin.Default())(implicit
      inputState: InputState,
      uiState: UiState
  ): Boolean =
    val (hot, active) = setHotActive(id, x, y, w, h)
    skin.renderButton(x, y, w, h, hot, active)
    hot && active && inputState.mouseDown == false

  def slider(id: ItemId, x: Int, y: Int, w: Int = 32, h: Int = 255, skin: SliderSkin = SliderSkin.Default())(
      value: Int,
      max: Int
  )(implicit inputState: InputState, uiState: UiState): Int =
    val padding       = 8 // FIXME
    val smallSide     = math.min(w, h)
    val largeSide     = math.max(w, h)
    val sliderSize    = smallSide - 2 * padding
    val maxPos        = largeSide - 2 * padding - sliderSize
    val pos           = (maxPos * value) / max
    val (hot, active) = setHotActive(id, x, y, w, h)
    skin.renderSlider(x, y, w, h, value, max, hot, active)
    if (active)
      val mouseAbsPos = (if (w > h) inputState.mouseX - x else inputState.mouseY - y) - padding - sliderSize / 2
      val mousePos    = math.max(0, math.min(mouseAbsPos, maxPos))
      val newValue    = (mousePos * max) / maxPos
      newValue
    else value
