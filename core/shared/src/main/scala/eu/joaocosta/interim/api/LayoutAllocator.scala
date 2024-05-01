package eu.joaocosta.interim.api

import eu.joaocosta.interim.{Font, HorizontalAlignment, Rect, TextLayout, VerticalAlignment}

/** A layout allocator is a side-effectful function that, given a size (width or height) tries to allocate
  * a new area.
  *
  * Note that calls to this function are side effectful, as each call reserves an area.
  */
trait LayoutAllocator:
  def area: Rect

  def allocate(width: Int, height: Int): Rect
  def allocate(text: String, font: Font, paddingW: Int = 0, paddingH: Int = 0): Rect =
    val textArea =
      TextLayout.computeArea(area.resize(-2 * paddingW, -2 * paddingH), text, font)
    allocate(textArea.w + 2 * paddingW, textArea.h + 2 * paddingH)

  def fill(): Rect = allocate(Int.MaxValue, Int.MaxValue)

object LayoutAllocator:
  trait RowAllocator extends LayoutAllocator:
    def nextRow(height: Int): Rect
    def allocate(width: Int, height: Int): Rect = nextRow(height)

  trait ColumnAllocator extends LayoutAllocator:
    def nextColumn(width: Int): Rect
    def allocate(width: Int, height: Int): Rect = nextColumn(width)

  final class DynamicRowAllocator(
      val area: Rect,
      padding: Int,
      alignment: VerticalAlignment.Top.type | VerticalAlignment.Bottom.type
  ) extends RowAllocator
      with (Int => Rect):
    private var currentY = area.y
    private var currentH = area.h
    private val dirMod   = if (alignment == VerticalAlignment.Top) 1 else -1

    def nextRow(height: Int) =
      val absHeight = math.abs(height).toInt
      if (absHeight == 0 || currentH <= 0) // Empty
        area.copy(y = currentY, h = 0)
      else if (absHeight >= currentH) // Fill remaining area
        val areaY = currentY
        val areaH = currentH
        currentY = area.h
        currentH = 0
        area.copy(y = areaY, h = areaH)
      else if (dirMod * height >= 0) // Fill from the top
        val areaY = currentY
        currentY += absHeight + padding
        currentH -= absHeight + padding
        area.copy(y = areaY, h = absHeight)
      else // Fill from the bottom
        val areaY = currentY + currentH - absHeight
        currentH -= absHeight + padding
        area.copy(y = areaY, h = absHeight)

    def apply(height: Int) = nextRow(height)

  final class StaticRowAllocator(
      val area: Rect,
      padding: Int,
      numRows: Int,
      alignment: VerticalAlignment.Top.type | VerticalAlignment.Bottom.type
  ) extends RowAllocator
      with IndexedSeq[Rect]:
    val cells: IndexedSeq[Rect] =
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

    def apply(i: Int): Rect = cells(i)
    val length              = cells.length

    private val cellsIterator = cells.iterator

    def nextRow(): Rect =
      if (!cellsIterator.hasNext) area.copy(w = 0, h = 0)
      else cellsIterator.next()

    def nextRow(height: Int): Rect =
      if (!cellsIterator.hasNext) area.copy(w = 0, h = 0)
      else
        var acc = cellsIterator.next()
        while (acc.h < height && cellsIterator.hasNext)
          acc = acc ++ cellsIterator.next()
        acc

  final class DynamicColumnAllocator(
      val area: Rect,
      padding: Int,
      alignment: HorizontalAlignment.Left.type | HorizontalAlignment.Right.type
  ) extends ColumnAllocator
      with (Int => Rect):
    private var currentX = area.x
    private var currentW = area.w
    private val dirMod   = if (alignment == HorizontalAlignment.Left) 1 else -1

    def nextColumn(width: Int): Rect =
      val absWidth = math.abs(width).toInt
      if (absWidth == 0 || currentW <= 0) // Empty
        area.copy(x = currentX, w = 0)
      else if (absWidth >= currentW) // Fill remaining area
        val areaX = currentX
        val areaW = currentW
        currentX = area.w
        currentW = 0
        area.copy(x = areaX, w = areaW)
      else if (dirMod * width >= 0) // Fill from the left
        val areaX = currentX
        currentX += absWidth + padding
        currentW -= absWidth + padding
        area.copy(x = areaX, w = absWidth)
      else // Fill from the right
        val areaX = currentX + currentW - absWidth
        currentW -= absWidth + padding
        area.copy(x = areaX, w = absWidth)

    def apply(height: Int) = nextColumn(height)

  final class StaticColumnAllocator(
      val area: Rect,
      padding: Int,
      numColumns: Int,
      alignment: HorizontalAlignment.Left.type | HorizontalAlignment.Right.type
  ) extends ColumnAllocator
      with IndexedSeq[Rect]:
    val cells: IndexedSeq[Rect] =
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

    def apply(i: Int): Rect = cells(i)
    val length              = cells.length

    private val cellsIterator = cells.iterator

    def nextColumn(): Rect =
      if (!cellsIterator.hasNext) area.copy(w = 0, h = 0)
      else cellsIterator.next()

    def nextColumn(width: Int): Rect =
      if (!cellsIterator.hasNext) area.copy(w = 0, h = 0)
      else
        var acc = cellsIterator.next()
        while (acc.w < width && cellsIterator.hasNext)
          acc = acc ++ cellsIterator.next()
        acc
