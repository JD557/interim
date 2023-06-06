package eu.joaocosta.interim.api

import eu.joaocosta.interim.Rect

object Layouts:
  def grid[T](area: Rect, numRows: Int, numColumns: Int, padding: Int)(body: Vector[Vector[Rect]] => T): T =
    body(rows(area, numRows, padding)(_.map(subArea => columns(subArea, numColumns, padding)(identity))))

  def rows[T](area: Rect, numRows: Int, padding: Int)(body: Vector[Rect] => T): T =
    val rowSize = (area.h - (numRows - 1) * padding) / numRows
    val vec = for
      row <- (0 until numRows)
      dy = row * (rowSize + padding)
    yield Rect(area.x, area.y + dy, area.w, rowSize)
    body(vec.toVector)

  def columns[T](area: Rect, numColumns: Int, padding: Int)(body: Vector[Rect] => T): T =
    val columnSize = (area.w - (numColumns - 1) * padding) / numColumns
    val vec = for
      column <- (0 until numColumns)
      dx = column * (columnSize + padding)
    yield Rect(area.x + dx, area.y, columnSize, area.h)
    body(vec.toVector)
