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
    * This method takes an input state and a UI context and mutates the UI context accordingly.
    * This should be called on every frame.
    *
    * The method returns a list of operations to render and the result of the body.
    */
  def ui[T](inputState: InputState, uiContext: UiContext)(
      run: (inputState: InputState, uiContext: UiContext) ?=> T
  ): (List[RenderOp], T) =
    // prepare
    uiContext.ops.clear()
    uiContext.hotItem = None
    if (inputState.mouseDown) uiContext.keyboardFocusItem = None
    // run
    val res = run(using inputState, uiContext)
    // finish
    if (!inputState.mouseDown) uiContext.activeItem = None
    // return
    (uiContext.ops.toList, res)
