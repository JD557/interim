package eu.joaocosta.interim

import scala.collection.mutable

class UiState private (
    private[interim] var hotItem: Option[ItemId],
    private[interim] var activeItem: Option[ItemId],
    private[interim] val ops: mutable.Queue[RenderOp]
):
  def this() = this(None, None, new mutable.Queue[RenderOp]())
  override def clone(): UiState = new UiState(hotItem, activeItem, ops.clone())
