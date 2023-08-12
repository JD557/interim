package eu.joaocosta.interim.api

import eu.joaocosta.interim.ItemId.*
import eu.joaocosta.interim.*
import eu.joaocosta.interim.skins.*

/** Objects containing all default panels.
  *
  * Panels are a mix of a component and a layout. They perform rendering operations, but also provide a draw area.
  *
  * By convention, all panels are of the form `def panel(id, area, params..., skin)(body): (Value, Rect)`.
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
    * @param movable if true, the window will include a move handle in the title bar
    */
  final def window[T](
      id: ItemId,
      area: Rect | Ref[Rect],
      title: String,
      movable: Boolean = false,
      skin: WindowSkin = WindowSkin.default(),
      handleSkin: HandleSkin = HandleSkin.default()
  )(
      body: Rect => T
  ): Components.Component[(T, Rect)] =
    val areaRef = area match {
      case ref: Ref[Rect] => ref
      case v: Rect        => Ref(v)
    }
    UiContext.registerItem(id, areaRef.get, passive = true)
    skin.renderWindow(areaRef.get, title)
    val res = body(skin.panelArea(areaRef.get))
    if (movable)
      Components
        .moveHandle(
          id |> "internal_move_handle",
          skin.titleTextArea(areaRef.get),
          handleSkin
        )(areaRef)
    (res, areaRef.get)
