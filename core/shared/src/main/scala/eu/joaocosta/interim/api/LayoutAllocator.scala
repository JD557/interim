package eu.joaocosta.interim.api

import eu.joaocosta.interim.{Font, Rect, TextLayout}

/** A layout allocator is a side-effectful function that, given a size (width or height) tries to allocate
  * a new area.
  *
  * Note that calls to this function are side effectful, as each call reserves an area.
  */
trait LayoutAllocator extends (Int => Rect):
  def allocate(size: Int): Rect
  def apply(size: Int): Rect = allocate(size)
  def allocate(text: String, font: Font): Rect

object LayoutAllocator:
  final class RowAllocator(area: Rect, padding: Int) extends LayoutAllocator:
    private var currentY = area.y
    private var currentH = area.h

    def allocate(height: Int) =
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

    def allocate(text: String, font: Font): Rect =
      val textArea = TextLayout.computeArea(area, text, font, (font.fontSize * 1.3).toInt)
      allocate(textArea.h)

  final class ColumnAllocator(area: Rect, padding: Int) extends LayoutAllocator:
    private var currentX = area.x
    private var currentW = area.w

    def allocate(width: Int): Rect =
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

    def allocate(text: String, font: Font): Rect =
      val textArea = TextLayout.computeArea(area, text, font, (font.fontSize * 1.3).toInt)
      allocate(textArea.w)
