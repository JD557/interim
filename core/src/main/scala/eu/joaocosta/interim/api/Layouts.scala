package eu.joaocosta.interim.api

import eu.joaocosta.interim.Rect

/** Objects containing all default layouts.
  *
  * By convention, all layouts are of the form `def layout(area, params...)(body): Value`.
  */
object Layouts extends Layouts

trait Layouts:

  /** Lays out the components in a grid where all elements have the same size, separated by a padding.
    *
    * The body receives a `cell: Vector[Vector[Rect]]`, where `cell(y)(x)` is the rect of the y-th row and x-th
    * column.
    */
  final def grid[T](area: Rect, numRows: Int, numColumns: Int, padding: Int)(body: Vector[Vector[Rect]] => T): T =
    body(rows(area, numRows, padding)(_.map(subArea => columns(subArea, numColumns, padding)(identity))))

  /** Lays out the components in a sequence of rows where all elements have the same size, separated by a padding.
    *
    * The body receives a `row: Vector[Rect]`, where `row(y)` is the rect of the y-th row.
    */
  final def rows[T](area: Rect, numRows: Int, padding: Int)(body: Vector[Rect] => T): T =
    val rowSize = (area.h - (numRows - 1) * padding) / numRows
    val vec = for
      row <- (0 until numRows)
      dy = row * (rowSize + padding)
    yield Rect(area.x, area.y + dy, area.w, rowSize)
    body(vec.toVector)

  /** Lays out the components in a sequence of columns where all elements have the same size, separated by a padding.
    *
    * The body receives a `column: Vector[Rect]`, where `column(y)` is the rect of the x-th column.
    */
  final def columns[T](area: Rect, numColumns: Int, padding: Int)(body: Vector[Rect] => T): T =
    val columnSize = (area.w - (numColumns - 1) * padding) / numColumns
    val vec = for
      column <- (0 until numColumns)
      dx = column * (columnSize + padding)
    yield Rect(area.x + dx, area.y, columnSize, area.h)
    body(vec.toVector)

  /** Lays out the components in a sequence of rows of different sizes, separated by a padding.
    *
    * The body receives a `nextRow: Int => Rect`, where `nextRow(height)` is the rect of the next row, with the
    * specified height (if possible).
    */
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

  /** Lays out the components in a sequence of columns of different sizes, separated by a padding.
    *
    * The body receives a `nextColumn: Int => Rect`, where `nextColumn(width)` is the rect of the next column, with the
    * specified width (if possible).
    */
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
