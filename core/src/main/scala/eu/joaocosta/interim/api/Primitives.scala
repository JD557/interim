package eu.joaocosta.interim.api

import eu.joaocosta.interim.TextLayout.{HorizontalAlignment, VerticalAlignment}
import eu.joaocosta.interim.{Color, Rect, RenderOp, UiState}

object Primitives extends Primitives

trait Primitives:
  final def rectangle(area: Rect, color: Color)(implicit uiState: UiState): Unit =
    uiState.ops.addOne(RenderOp.DrawRect(area, color))

  final def text(
      area: Rect,
      color: Color,
      text: String,
      fontSize: Int,
      horizontalAlignment: HorizontalAlignment = HorizontalAlignment.Left,
      verticalAlignment: VerticalAlignment = VerticalAlignment.Top
  )(implicit
      uiState: UiState
  ): Unit =
    if (text.nonEmpty)
      uiState.ops.addOne(RenderOp.DrawText(area, color, text, fontSize, horizontalAlignment, verticalAlignment))

  final def custom[T](area: Rect, color: Color, data: T)(implicit uiState: UiState): Unit =
    uiState.ops.addOne(RenderOp.Custom(area, color, data))
