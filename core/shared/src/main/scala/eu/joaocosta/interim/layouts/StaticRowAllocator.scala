package eu.joaocosta.interim.layouts

import eu.joaocosta.interim._

final class StaticRowAllocator(
    val area: Rect,
    padding: Int,
    numRows: Int,
    alignment: VerticalAlignment.Top.type | VerticalAlignment.Bottom.type
) extends LayoutAllocator.RowAllocator
    with LayoutAllocator.AreaAllocator
    with LayoutAllocator.CellAllocator:
  lazy val cells: IndexedSeq[Rect] =
    if (numRows == 0) Vector.empty
    else
      val rowSize    = (area.h - (numRows - 1) * padding) / numRows.toDouble
      val intRowSize = rowSize.toInt
      val baseCells = for
        row <- (0 until numRows)
        dy = (row * (rowSize + padding)).toInt
      yield Rect(area.x, area.y + dy, area.w, intRowSize)
      if (alignment == VerticalAlignment.Top) baseCells
      else baseCells.reverse

  def nextRow(): Rect = nextCell()

  def nextRow(height: Int): Rect =
    if (!cellsIterator.hasNext) area.copy(w = 0, h = 0)
    else
      var acc = cellsIterator.next()
      while (acc.h < height && cellsIterator.hasNext)
        acc = acc ++ cellsIterator.next()
      acc
