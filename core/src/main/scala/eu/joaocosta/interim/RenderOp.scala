package eu.joaocosta.interim

enum RenderOp:
  case DrawRect(area: Rect, color: Color)
  case DrawText(
      area: Rect,
      color: Color,
      text: String,
      fontSize: Int,
      horizontalAlignment: TextLayout.HorizontalAlignment,
      verticalAlignment: TextLayout.VerticalAlignment
  )
  case Custom[T](area: Rect, color: Color, data: T)

object RenderOp:
  final case class DrawChar(area: Rect, color: Color, char: Char)

  extension (textOp: DrawText)
    def asDrawChars(
        charWidth: Char => Int = _ => textOp.fontSize,
        lineHeight: Int = (textOp.fontSize * 1.3).toInt
    ): List[DrawChar] = TextLayout.asDrawChars(textOp, charWidth, lineHeight)
