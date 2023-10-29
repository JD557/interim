package eu.joaocosta.interim.api

import eu.joaocosta.interim.*
import eu.joaocosta.interim.skins.*

/** Objects containing all default panels.
  *
  * Panels are a mix of a component and a layout. They perform rendering operations, but also provide a draw area.
  *
  * By convention, all panels are of the form `def panel(id, area, params..., skin)(body): (Option[Value], PanelState[Rect])`.
  * The returned value is the value returned by the body. Panels also return a rect, which is the area
  * the panel must be called with in the next frame (e.g. for movable panels).
  *
  * As such, panels should be called like:
  *
  * ```
  *  val (value, nextRect) = panel(id, panelRect, ...) {area => ...}
  *  panelRect = nextRect
  * ```
  */
object Panels extends Panels

trait Panels:

  /**  Window with a title.
    *
    * @param title of this window
    * @param closable if true, the window will include a closable handle in the title bar
    * @param movable if true, the window will include a move handle in the title bar
    * @param resizable if true, the window will include a resize handle in the bottom corner
    */
  final def window[T](
      id: ItemId,
      area: Rect | PanelState[Rect] | Ref[PanelState[Rect]],
      title: String,
      closable: Boolean = false,
      movable: Boolean = false,
      resizable: Boolean = false,
      skin: WindowSkin = WindowSkin.default(),
      handleSkin: HandleSkin = HandleSkin.default()
  )(
      body: Rect => T
  ): Components.Component[(Option[T], PanelState[Rect])] =
    val panelStateRef = area match
      case ref: Ref[PanelState[Rect]] => ref
      case v: PanelState[Rect]        => Ref(v)
      case v: Rect                    => Ref(PanelState.open(v))
    if (panelStateRef.get.isOpen)
      def windowArea = panelStateRef.get.value
      UiContext.registerItem(id, windowArea, passive = true)
      skin.renderWindow(windowArea, title)
      val res = body(skin.panelArea(windowArea))
      if (closable)
        Components
          .closeHandle(
            id |> "internal_close_handle",
            skin.titleTextArea(windowArea),
            handleSkin
          )(panelStateRef)
      if (resizable)
        val newArea = Components
          .resizeHandle(
            id |> "internal_resize_handle",
            skin.resizeArea(windowArea),
            handleSkin
          )(windowArea)
        panelStateRef.modify(_.copy(value = skin.ensureMinimumArea(newArea)))
      if (movable)
        val newArea = Components
          .moveHandle(
            id |> "internal_move_handle",
            skin.titleTextArea(windowArea),
            handleSkin
          )(windowArea)
        panelStateRef.modify(_.copy(value = newArea))
      (Some(res), panelStateRef.get)
    else (None, panelStateRef.get)
