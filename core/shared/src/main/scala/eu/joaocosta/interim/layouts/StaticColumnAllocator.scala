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
      val columnSize    = (area.w - (numColumns - 1) * padding) / numColumns.toDouble
      val intColumnSize = columnSize.toInt
      val baseCells = for
        column <- (0 until numColumns)
        dx = (column * (columnSize + padding)).toInt
      yield Rect(area.x + dx, area.y, intColumnSize, area.h)
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
