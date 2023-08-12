package eu.joaocosta.interim.api

import eu.joaocosta.interim.{InputState, Rect, UiContext}

/** Objects containing all default layouts.
  *
  * By convention, all layouts are of the form `def layout(area, params...)(body): Value`.
  */
object Layouts extends Layouts

trait Layouts:

  /** Clipped region.
    *
    * The body will be rendered only inside the clipped area. Input outside the area will also be ignored.
    */
  final def clip[T](area: Rect)(
      body: (InputState, UiContext) ?=> T
  )(using inputState: InputState, uiContext: UiContext): T =
    val newUiContext = uiContext.fork()
    val newInputState =
      if (area.isMouseOver) inputState
      else inputState.copy(mouseX = Int.MinValue, mouseY = Int.MinValue)
    val result = body(using newInputState, newUiContext)
    newUiContext.ops.foreach(_._2.mapInPlace(_.clip(area)).filterInPlace(!_.area.isEmpty))
    uiContext ++= newUiContext
    result

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
    if (numRows <= 0) body(Vector.empty)
    else
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
    if (numColumns <= 0) body(Vector.empty)
    else
      val columnSize = (area.w - (numColumns - 1) * padding) / numColumns
      val vec = for
        column <- (0 until numColumns)
        dx = column * (columnSize + padding)
      yield Rect(area.x + dx, area.y, columnSize, area.h)
      body(vec.toVector)

  /** Lays out the components in a sequence of rows of different sizes, separated by a padding.
    *
    * The body receives a `nextRow: Int => Rect`, where `nextRow(height)` is the rect of the next row, with the
    * specified height (if possible). If the size is negative, the row will start from the bottom.
    */
  final def dynamicRows[T](area: Rect, padding: Int)(body: (Int => Rect) => T): T =
    var currentY = area.y
    var currentH = area.h
    def generateRect(height: Int): Rect =
      val absHeight = math.abs(height).toInt
      if (absHeight == 0 || currentH <= 0) // Empty
        area.copy(y = currentY, h = 0)
      else if (absHeight >= currentH) // Fill remaining area
        val areaY = currentY
        val areaH = currentH
        currentY = area.h
        currentH = 0
        area.copy(y = areaY, h = areaH)
      else if (height >= 0) // Fill from the top
        val areaY = currentY
        currentY += absHeight + padding
        currentH -= absHeight + padding
        area.copy(y = areaY, h = absHeight)
      else // Fill from the bottom
        val areaY = currentY + currentH - absHeight
        currentH -= absHeight + padding
        area.copy(y = areaY, h = absHeight)
    body(generateRect)

  /** Lays out the components in a sequence of columns of different sizes, separated by a padding.
    *
    * The body receives a `nextColumn: Int => Rect`, where `nextColumn(width)` is the rect of the next column, with the
    * specified width (if possible). . If the size is negative, the row will start from the right.
    */
  final def dynamicColumns[T](area: Rect, padding: Int)(body: (Int => Rect) => T): T =
    var currentX = area.x
    var currentW = area.w
    def generateRect(width: Int): Rect =
      val absWidth = math.abs(width).toInt
      if (absWidth == 0 || currentW <= 0) // Empty
        area.copy(x = currentX, w = 0)
      else if (absWidth >= currentW) // Fill remaining area
        val areaX = currentX
        val areaW = currentW
        currentX = area.w
        currentW = 0
        area.copy(x = areaX, w = areaW)
      else if (width >= 0) // Fill from the left
        val areaX = currentX
        currentX += absWidth + padding
        currentW -= absWidth + padding
        area.copy(x = areaX, w = absWidth)
      else // Fill from the right
        val areaX = currentX + currentW - absWidth
        currentW -= absWidth + padding
        area.copy(x = areaX, w = absWidth)
    body(generateRect)
