package eu.joaocosta.interim

enum RenderOp:
  case DrawRect(area: Rect, color: Color)
  case DrawText(
      area: Rect,
      text: String,
      fontSize: Int,
      color: Color,
      horizontalAlignment: TextLayout.HorizontalAlignment,
      verticalAlignment: TextLayout.VerticalAlignment
  )

object RenderOp:
  final case class DrawChar(area: Rect, char: Char, color: Color)

  extension (textOp: DrawText)
    def asDrawChars(
        charWidth: Char => Int = _ => textOp.fontSize,
        lineHeight: Int = (textOp.fontSize * 1.3).toInt
    ): List[DrawChar] = TextLayout.asDrawChars(textOp, charWidth, lineHeight)
