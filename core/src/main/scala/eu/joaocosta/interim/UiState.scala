package eu.joaocosta.interim

import scala.collection.mutable

final class UiState private (
    private[interim] var hotItem: Option[ItemId],
    private[interim] var activeItem: Option[ItemId],
    private[interim] var keyboardFocusItem: Option[ItemId],
    private[interim] val ops: mutable.Queue[RenderOp]
):
  def this() = this(None, None, None, new mutable.Queue[RenderOp]())
  override def clone(): UiState = new UiState(hotItem, activeItem, keyboardFocusItem, ops.clone())
  private def registerItem(id: ItemId, area: Rect)(implicit inputState: InputState): UiState.ItemStatus =
    if (area.isMouseOver)
      hotItem = Some(id)
      if ((activeItem == None || activeItem == Some(id)) && inputState.mouseDown)
        activeItem = Some(id)
        keyboardFocusItem = Some(id)
    UiState.ItemStatus(hotItem == Some(id), activeItem == Some(id), keyboardFocusItem == Some(id))

object UiState:
  final case class ItemStatus(hot: Boolean, active: Boolean, keyboardFocus: Boolean)

  def registerItem(id: ItemId, area: Rect)(implicit uiState: UiState, inputState: InputState): UiState.ItemStatus =
    uiState.registerItem(id, area)
