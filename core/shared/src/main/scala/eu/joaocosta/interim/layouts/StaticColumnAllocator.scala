package eu.joaocosta.interim.layouts

import eu.joaocosta.interim._

final class StaticColumnAllocator(
    val area: Rect,
    padding: Int,
    numColumns: Int,
    alignment: HorizontalAlignment.Left.type | HorizontalAlignment.Right.type
) extends LayoutAllocator.ColumnAllocator
    with LayoutAllocator.AreaAllocator
    with LayoutAllocator.CellAllocator:
  lazy val cells: IndexedSeq[Rect] =
    if (numColumns == 0) Vector.empty
    else
      val totalInnerArea = (area.w - (numColumns - 1) * padding)
      val columnSize     = totalInnerArea / numColumns
      val baseCells      = Vector.tabulate(numColumns)(column =>
        val dx = (column * totalInnerArea) / numColumns + (column * padding)
        Rect(area.x + dx, area.y, columnSize, area.h)
      )
      if (alignment == HorizontalAlignment.Left) baseCells
      else baseCells.reverse

  def nextColumn(): Rect = nextCell()

  def nextColumn(width: Int): Rect =
    if (!cellsIterator.hasNext) area.copy(w = 0, h = 0)
    else
      var acc = cellsIterator.next()
      while (acc.w < width && cellsIterator.hasNext)
        acc = acc ++ cellsIterator.next()
      acc
