package eu.joaocosta.interim

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
