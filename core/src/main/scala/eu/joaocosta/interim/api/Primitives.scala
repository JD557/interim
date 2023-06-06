package eu.joaocosta.interim.api

import eu.joaocosta.interim.TextLayout.{HorizontalAlignment, VerticalAlignment}
import eu.joaocosta.interim.{Color, Rect, RenderOp, UiState}

object Primitives extends Primitives

trait Primitives:
  final def rectangle(area: Rect, color: Color)(implicit uiState: UiState): Unit =
    uiState.ops.addOne(RenderOp.DrawRect(area, color))

  final def text(
      area: Rect,
      text: String,
      fontSize: Int,
      color: Color,
      horizontalAlignment: HorizontalAlignment = HorizontalAlignment.Left,
      verticalAlignment: VerticalAlignment = VerticalAlignment.Top
  )(implicit
      uiState: UiState
  ): Unit =
    if (text.nonEmpty)
      uiState.ops.addOne(RenderOp.DrawText(area, text, fontSize, color, horizontalAlignment, verticalAlignment))
