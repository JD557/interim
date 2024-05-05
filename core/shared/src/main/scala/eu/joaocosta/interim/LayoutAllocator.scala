package eu.joaocosta.interim

import eu.joaocosta.interim._

/** A layout allocator is a side-effectful function that tries to allocate some space inside of an area.
  *
  * Note that calls to this function are side effectful, as each call reserves an area.
  */
sealed trait LayoutAllocator:
  def area: Rect

object LayoutAllocator:
  /** Allocator that allows one to allocate space based on a required area.
    */
  trait AreaAllocator extends LayoutAllocator:
    def allocate(width: Int, height: Int): Rect
    def fill(): Rect = allocate(Int.MaxValue, Int.MaxValue)
    def allocate(text: String, font: Font, paddingW: Int = 0, paddingH: Int = 0): Rect =
      val textArea =
        TextLayout.computeArea(area.resize(-2 * paddingW, -2 * paddingH), text, font)
      allocate(textArea.w + 2 * paddingW, textArea.h + 2 * paddingH)

  /** Allocator that allows one to request new cells.
    *
    * The preallocated cells can also be accessed as an `IndexedSeq`
    */
  trait CellAllocator extends LayoutAllocator with IndexedSeq[Rect]:
    lazy val cells: IndexedSeq[Rect]
    protected lazy val cellsIterator = cells.iterator

    def apply(i: Int): Rect = cells(i)
    val length              = cells.length

    def nextCell(): Rect =
      if (!cellsIterator.hasNext) area.copy(w = 0, h = 0)
      else cellsIterator.next()

  trait RowAllocator extends LayoutAllocator:
    def nextRow(height: Int): Rect
    def allocate(width: Int, height: Int): Rect = nextRow(height)

  trait ColumnAllocator extends LayoutAllocator:
    def nextColumn(width: Int): Rect
    def allocate(width: Int, height: Int): Rect = nextColumn(width)
