package eu.joaocosta.interim.api

import eu.joaocosta.interim.TextLayout.{HorizontalAlignment, VerticalAlignment}
import eu.joaocosta.interim.{Color, Rect, RenderOp, UiState}

/** Object containing the default primitives.
  *
  * By convention, all components are functions in the form `def primitive(area, color, params...): Unit`.
  */
object Primitives extends Primitives

trait Primitives:
  /** Draws a rectangle filling a the specified area with a color.
    */
  final def rectangle(area: Rect, color: Color)(using uiState: UiState): Unit =
    uiState.ops.addOne(RenderOp.DrawRect(area, color))

  /** Draws a block of text in the specified area with a color.
    *
    * @param text text to write
    * @param fontSize font size in px
    * @param horizontalAlignment how the text should be aligned horizontally
    * @param verticalAlignment how the text should be aligned vertically
    */
  final def text(
      area: Rect,
      color: Color,
      text: String,
      fontSize: Int,
      horizontalAlignment: HorizontalAlignment = HorizontalAlignment.Left,
      verticalAlignment: VerticalAlignment = VerticalAlignment.Top
  )(using
      uiState: UiState
  ): Unit =
    if (text.nonEmpty)
      uiState.ops.addOne(RenderOp.DrawText(area, color, text, fontSize, area, horizontalAlignment, verticalAlignment))

  /** Advanced operation to add a custom primitive to the list of render operations.
    *
    * Supports an arbitrary data value. It's up to the backend to interpret it as it sees fit.
    * If the backend does not know how to interpret it, it can just render a colored rect.
    *
    * @param data custom value to be interpreted by the backend.
    */
  final def custom[T](area: Rect, color: Color, data: T)(using uiState: UiState): Unit =
    uiState.ops.addOne(RenderOp.Custom(area, color, data))
