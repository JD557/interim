package eu.joaocosta.interim.api

import eu.joaocosta.interim._
import eu.joaocosta.interim.skins._

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
  *  val (value, nextRect) = panel(id, params..., skins...)(panelRect){area => ...}
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
  final def window(
      id: ItemId,
      title: String,
      closable: Boolean = false,
      movable: Boolean = false,
      resizable: Boolean = false,
      skin: WindowSkin = WindowSkin.default(),
      handleSkin: HandleSkin = HandleSkin.default()
  ): Panel[Rect, [T] =>> (Option[T], PanelState[Rect])] =
    new Panel[Rect, [T] =>> (Option[T], PanelState[Rect])]:
      def render[T](area: Ref[PanelState[Rect]], body: Rect => T): Component[(Option[T], PanelState[Rect])] =
        if (area.get.isOpen)
          def windowArea = area.get.value
          UiContext.registerItem(id, windowArea, passive = true)
          skin.renderWindow(windowArea, title)
          val res = body(skin.panelArea(windowArea))
          if (closable)
            Components
              .closeHandle(
                id |> "internal_close_handle",
                handleSkin
              )(skin.titleTextArea(windowArea), area)
          if (resizable)
            val newArea = Components
              .resizeHandle(
                id |> "internal_resize_handle",
                handleSkin
              )(skin.resizeArea(windowArea), windowArea)
            area.modify(_.copy(value = skin.ensureMinimumArea(newArea)))
          if (movable)
            val newArea = Components
              .moveHandle(
                id |> "internal_move_handle",
                handleSkin
              )(skin.titleTextArea(windowArea), windowArea)
            area.modify(_.copy(value = newArea))
          (Option.when(area.get.isOpen)(res), area.get)
        else (None, area.get)
