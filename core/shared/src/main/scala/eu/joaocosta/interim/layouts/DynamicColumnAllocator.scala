package eu.joaocosta.interim.layouts

import eu.joaocosta.interim._

final class DynamicColumnAllocator(
    val area: Rect,
    padding: Int,
    alignment: HorizontalAlignment.Left.type | HorizontalAlignment.Right.type
) extends LayoutAllocator.ColumnAllocator
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
