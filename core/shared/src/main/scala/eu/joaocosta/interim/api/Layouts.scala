package eu.joaocosta.interim.api

import eu.joaocosta.interim.api.LayoutAllocator._
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
    *
    * Note that the clip will only be applied to elements in the current Z-index.
    */
  final def clip[T](area: Rect)(
      body: (InputState, UiContext) ?=> T
  )(using inputState: InputState, uiContext: UiContext): T =
    val newUiContext  = uiContext.fork()
    val newInputState = inputState.clip(area)
    val result        = body(using newInputState, newUiContext)
    newUiContext.ops.get(newUiContext.currentZ).foreach(_.mapInPlace(_.clip(area)).filterInPlace(!_.area.isEmpty))
    uiContext ++= newUiContext
    result

  /** Lays out the components in a grid where all elements have the same size, separated by a padding.
    *
    * The body receives a `cell: Vector[Vector[Rect]]`, where `cell(y)(x)` is the rect of the y-th row and x-th
    * column.
    */
  final def grid[T](area: Rect, numRows: Int, numColumns: Int, padding: Int)(
      body: IndexedSeq[IndexedSeq[Rect]] => T
  ): T =
    body(
      rows(area, numRows, padding)(rowAlloc ?=> rowAlloc.map(subArea => columns(subArea, numColumns, padding)(summon)))
    )

  /** Lays out the components in a sequence of rows where all elements have the same size, separated by a padding.
    *
    * The body receives a `row: Vector[Rect]`, where `row(y)` is the rect of the y-th row.
    */
  final def rows[T](area: Rect, numRows: Int, padding: Int)(body: StaticRowAllocator ?=> T): T =
    val allocator = new StaticRowAllocator(area, padding, numRows)
    body(using new StaticRowAllocator(area, padding, numRows))

  /** Lays out the components in a sequence of columns where all elements have the same size, separated by a padding.
    *
    * The body receives a `column: Vector[Rect]`, where `column(y)` is the rect of the x-th column.
    */
  final def columns[T](area: Rect, numColumns: Int, padding: Int)(body: StaticColumnAllocator ?=> T): T =
    val allocator = new StaticColumnAllocator(area, padding, numColumns)
    body(using allocator)

  /** Lays out the components in a sequence of rows of different sizes, separated by a padding.
    *
    * The body receives a `nextRow: Int => Rect`, where `nextRow(height)` is the rect of the next row, with the
    * specified height (if possible). If the size is negative, the row will start from the bottom.
    */
  final def dynamicRows[T](area: Rect, padding: Int)(body: DynamicRowAllocator ?=> T): T =
    val allocator = new LayoutAllocator.DynamicRowAllocator(area, padding)
    body(using allocator)

  /** Lays out the components in a sequence of columns of different sizes, separated by a padding.
    *
    * The body receives a `nextColumn: Int => Rect`, where `nextColumn(width)` is the rect of the next column, with the
    * specified width (if possible). . If the size is negative, the row will start from the right.
    */
  final def dynamicColumns[T](area: Rect, padding: Int)(body: DynamicColumnAllocator ?=> T): T =
    val allocator = new LayoutAllocator.DynamicColumnAllocator(area, padding)
    body(using allocator)

  /** Handle mouse events inside a specified area.
    *
    * The body receives an optional MouseInput with the coordinates adjusted to be relative
    * to the enclosing area.
    * If the mouse is outside of the area, the body receives None.
    */
  final def mouseArea[T](area: Rect)(body: Option[InputState.MouseInput] => T)(using inputState: InputState): T =
    body(
      Option.when(area.isMouseOver)(
        inputState.mouseInput.copy(position = inputState.mouseInput.position.map((x, y) => (x - area.x, y - area.y)))
      )
    )
