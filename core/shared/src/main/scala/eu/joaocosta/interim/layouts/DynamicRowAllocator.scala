package eu.joaocosta.interim.layouts

import eu.joaocosta.interim._

final class DynamicRowAllocator(
    val area: Rect,
    padding: Int,
    alignment: VerticalAlignment.Top.type | VerticalAlignment.Bottom.type
) extends LayoutAllocator.RowAllocator
    with LayoutAllocator.AreaAllocator
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
