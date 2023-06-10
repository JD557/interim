package eu.joaocosta.interim

import eu.joaocosta.interim.TextLayout.*

/** Object with all the DSL operations.
  *
  * Most applications will only need to import `eu.joaocosta.interim._` and `eu.joaocosta.interim.InterIm._`.
  * However, since some of the methods in the DSL can conflict with other variable names, it can be desirable to not
  * import the DSL and explicitly use the InterIm prefix (e.g. `IterIm.text` instead of `text`)
  */
object InterIm extends api.Primitives with api.Layouts with api.Components with api.Constants with api.Panels:
  /** Wraps the UI interactions. All API calls should happen inside the body (run parameter).
    *
    * This method takes an input state and a UI state and mutates the UI state accordingly.
    * This should be called on every frame.
    *
    * The method returns a list of operations to render and the result of the body.
    */
  def ui[T](inputState: InputState, uiState: UiState)(
      run: (inputState: InputState, uiState: UiState) ?=> T
  ): (List[RenderOp], T) =
    // prepare
    uiState.ops.clear()
    uiState.hotItem = None
    if (inputState.mouseDown) uiState.keyboardFocusItem = None
    // run
    given is: InputState = inputState
    given us: UiState    = uiState
    val res              = run
    // finish
    if (!inputState.mouseDown) uiState.activeItem = None
    // return
    (uiState.ops.toList, res)
