package eu.joaocosta.interim

import eu.joaocosta.interim.TextLayout.*

object InterIm extends api.Primitives with api.Layouts with api.Components with api.Constants:
  def window[T](inputState: InputState, uiState: UiState)(
      run: (inputState: InputState, uiState: UiState) ?=> T
  ): (List[RenderOp], T) =
    // prepare
    uiState.ops.clear()
    uiState.hotItem = None
    // run
    given is: InputState = inputState
    given us: UiState    = uiState
    val res              = run
    // finish
    if (!inputState.mouseDown) uiState.activeItem = None
    // return
    (uiState.ops.toList, res)
