package eu.joaocosta.guila

import scala.annotation.tailrec

enum RenderOp:
  case DrawRect(area: Rect, color: Color)
  case DrawText(area: Rect, text: String, color: Color)

object RenderOp:
  final case class DrawChar(area: Rect, char: Char, color: Color)

  extension (textOp: DrawText)
    def asDrawChars(charWidth: Char => Int, charHeight: Int, lineHeight: Int): List[DrawChar] =
      @tailrec
      def layout(remaining: List[Char], dx: Int, dy: Int, acc: List[DrawChar]): List[DrawChar] =
        remaining match {
          case Nil => acc.reverse
          case char :: cs =>
            val width = charWidth(char)
            if (dy + charHeight > textOp.area.h) layout(Nil, dx, dy, acc) // End here
            else if (width < textOp.area.w && dx + width > textOp.area.w) // Newline
              layout(remaining, 0, dy + lineHeight, acc)
            else
              val charArea = Rect(
                x = textOp.area.x + dx,
                y = textOp.area.y + dy,
                w = width,
                h = charHeight
              )
              layout(cs, dx + width, dy, DrawChar(charArea, char, textOp.color) :: acc)
        }
      layout(textOp.text.toList, 0, 0, Nil)
