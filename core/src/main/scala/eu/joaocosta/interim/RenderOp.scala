package eu.joaocosta.interim

import scala.annotation.tailrec

enum RenderOp:
  case DrawRect(area: Rect, color: Color)
  case DrawText(
      area: Rect,
      text: String,
      fontSize: Int,
      color: Color,
      center: Boolean
  )

object RenderOp:
  final case class DrawChar(area: Rect, char: Char, color: Color)

  extension (textOp: DrawText)
    def asDrawChars(
        charWidth: Char => Int = _ => textOp.fontSize,
        lineHeight: Int = (textOp.fontSize * 1.3).toInt
    ): List[DrawChar] =
      def centerH(chars: List[DrawChar]): List[DrawChar] =
        val minX   = chars.map(_.area.x).minOption.getOrElse(0)
        val maxX   = chars.map(c => c.area.x + c.area.w).maxOption.getOrElse(0)
        val deltaX = (textOp.area.w - (maxX - minX)) / 2
        chars.map(c => c.copy(area = c.area.copy(x = c.area.x + deltaX)))
      def centerV(chars: List[DrawChar]): List[DrawChar] =
        val minY   = chars.map(_.area.y).minOption.getOrElse(0)
        val maxY   = chars.map(c => c.area.y + c.area.h).maxOption.getOrElse(0)
        val deltaY = (textOp.area.h - (maxY - minY)) / 2
        chars.map(c => c.copy(area = c.area.copy(y = c.area.y + deltaY)))
      @tailrec
      def layout(
          remaining: List[Char],
          dx: Int,
          dy: Int,
          lineAcc: List[DrawChar],
          textAcc: List[DrawChar]
      ): List[DrawChar] =
        remaining match {
          case Nil =>
            if (textOp.center) centerV(centerH(lineAcc) ++ textAcc) else lineAcc ++ textAcc
          case char :: cs =>
            val isNewline = char == '\n'
            val width     = charWidth(char)
            if (dy + textOp.fontSize > textOp.area.h) layout(Nil, dx, dy, lineAcc, textAcc) // End here
            else if (isNewline || width < textOp.area.w && dx + width > textOp.area.w)      // Newline
              val line = if (textOp.center) centerH(lineAcc) else lineAcc
              layout(if (isNewline) remaining.tail else remaining, 0, dy + lineHeight, Nil, line ++ textAcc)
            else
              val charArea = Rect(
                x = textOp.area.x + dx,
                y = textOp.area.y + dy,
                w = width,
                h = textOp.fontSize
              )
              layout(cs, dx + width, dy, DrawChar(charArea, char, textOp.color) :: lineAcc, textAcc)
        }
      layout(textOp.text.toList, 0, 0, Nil, Nil)
