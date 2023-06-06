package eu.joaocosta.interim.api

import eu.joaocosta.interim.Rect

object Layouts extends Layouts

trait Layouts:
  final def grid[T](area: Rect, numRows: Int, numColumns: Int, padding: Int)(body: Vector[Vector[Rect]] => T): T =
    body(rows(area, numRows, padding)(_.map(subArea => columns(subArea, numColumns, padding)(identity))))

  final def rows[T](area: Rect, numRows: Int, padding: Int)(body: Vector[Rect] => T): T =
    val rowSize = (area.h - (numRows - 1) * padding) / numRows
    val vec = for
      row <- (0 until numRows)
      dy = row * (rowSize + padding)
    yield Rect(area.x, area.y + dy, area.w, rowSize)
    body(vec.toVector)

  final def columns[T](area: Rect, numColumns: Int, padding: Int)(body: Vector[Rect] => T): T =
    val columnSize = (area.w - (numColumns - 1) * padding) / numColumns
    val vec = for
      column <- (0 until numColumns)
      dx = column * (columnSize + padding)
    yield Rect(area.x + dx, area.y, columnSize, area.h)
    body(vec.toVector)

  final def dynamicRows[T](area: Rect, padding: Int)(body: (Int => Rect) => T): T =
    var remainingSize = area.h
    def generateRect(height: Int): Rect =
      val y = area.y + area.h - remainingSize
      if (remainingSize >= height + padding)
        remainingSize -= (height + padding)
        area.copy(y = y, h = height)
      else
        val remainders = remainingSize
        remainingSize = 0
        area.copy(y = y, h = remainingSize)
    body(generateRect)

  final def dynamicColumns[T](area: Rect, padding: Int)(body: (Int => Rect) => T): T =
    var remainingSize = area.w
    def generateRect(width: Int): Rect =
      val x = area.x + area.w - remainingSize
      if (remainingSize >= width + padding)
        remainingSize -= (width + padding)
        area.copy(x = x, w = width)
      else
        val remainders = remainingSize
        remainingSize = 0
        area.copy(x = x, w = remainingSize)
    body(generateRect)
