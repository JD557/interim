package eu.joaocosta.interim

import scala.collection.mutable

/** Internal state of the UI.
  *
  * This object keeps the mutable state of the UI and should not be manipulated manually.
  *
  * Instead, it should be created with `new UiState()` at the start of the application and passed on every frame to
  * [[eu.joaocosta.interim.InterIm.ui]].
  */
final class UiState private (
    private[interim] var hotItem: Option[ItemId],
    private[interim] var activeItem: Option[ItemId],
    private[interim] var keyboardFocusItem: Option[ItemId],
    private[interim] val ops: mutable.Queue[RenderOp]
):
  def this() = this(None, None, None, new mutable.Queue[RenderOp]())
  override def clone(): UiState = new UiState(hotItem, activeItem, keyboardFocusItem, ops.clone())
  private def registerItem(id: ItemId, area: Rect)(using inputState: InputState): UiState.ItemStatus =
    if (area.isMouseOver)
      hotItem = Some(id)
      if ((activeItem == None || activeItem == Some(id)) && inputState.mouseDown)
        activeItem = Some(id)
        keyboardFocusItem = Some(id)
    UiState.ItemStatus(hotItem == Some(id), activeItem == Some(id), keyboardFocusItem == Some(id))

object UiState:
  /** Status of an item.
    *
    *  @param hot if the mouse is on top of the item
    *  @param active if the mouse clicked the item (and is still pressed down)
    *  @param keyboardFocus if the keyboard events should be consumed by this item
    */
  final case class ItemStatus(hot: Boolean, active: Boolean, keyboardFocus: Boolean)

  /** Registers an item on the UI state, taking a certain area.
    *
    * Components register themselves on every frame to update and check their status.
    *
    * Note that this is only required when creating new components.
    *
    * @return the item status of the registered component.
    */
  def registerItem(id: ItemId, area: Rect)(using uiState: UiState, inputState: InputState): UiState.ItemStatus =
    uiState.registerItem(id, area)
