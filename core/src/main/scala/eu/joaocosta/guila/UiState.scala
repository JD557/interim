package eu.joaocosta.guila

import scala.collection.mutable

class UiState private (
    private[guila] var hotItem: Option[ItemId],
    private[guila] var activeItem: Option[ItemId],
    private[guila] val ops: mutable.Queue[RenderOp]
):
  def this() = this(None, None, new mutable.Queue[RenderOp]())
  override def clone(): UiState = new UiState(hotItem, activeItem, ops.clone())
