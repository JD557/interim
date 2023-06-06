package eu.joaocosta.interim

import scala.annotation.tailrec

object TextLayout:
  enum HorizontalAlignment:
    case Left, Center, Right

  enum VerticalAlignment:
    case Top, Center, Bottom

  private def alignH(
      chars: List[RenderOp.DrawChar],
      areaWidth: Int,
      alignment: HorizontalAlignment
  ): List[RenderOp.DrawChar] =
    val minX   = chars.map(_.area.x).minOption.getOrElse(0)
    val maxX   = chars.map(c => c.area.x + c.area.w).maxOption.getOrElse(0)
    val deltaX = alignment.ordinal * (areaWidth - (maxX - minX)) / 2
    chars.map(c => c.copy(area = c.area.copy(x = c.area.x + deltaX)))

  private def alignV(
      chars: List[RenderOp.DrawChar],
      areaHeight: Int,
      alignment: VerticalAlignment
  ): List[RenderOp.DrawChar] =
    val minY   = chars.map(_.area.y).minOption.getOrElse(0)
    val maxY   = chars.map(c => c.area.y + c.area.h).maxOption.getOrElse(0)
    val deltaY = alignment.ordinal * (areaHeight - (maxY - minY)) / 2
    chars.map(c => c.copy(area = c.area.copy(y = c.area.y + deltaY)))

  private[interim] def asDrawChars(
      textOp: RenderOp.DrawText,
      charWidth: Char => Int,
      lineHeight: Int
  ): List[RenderOp.DrawChar] =
    @tailrec
    def layout(
        remaining: List[Char],
        dx: Int,
        dy: Int,
        lineAcc: List[RenderOp.DrawChar],
        textAcc: List[RenderOp.DrawChar]
    ): List[RenderOp.DrawChar] =
      remaining match {
        case Nil =>
          alignV(
            alignH(lineAcc, textOp.area.w, textOp.horizontalAlignment) ++ textAcc,
            textOp.area.h,
            textOp.verticalAlignment
          )
        case char :: cs =>
          val isNewline = char == '\n'
          val width     = charWidth(char)
          if (dy + textOp.fontSize > textOp.area.h) layout(Nil, dx, dy, lineAcc, textAcc) // End here
          else if (isNewline || width < textOp.area.w && dx + width > textOp.area.w)      // Newline
            val line = alignH(lineAcc, textOp.area.h, textOp.horizontalAlignment)
            layout(if (isNewline) remaining.tail else remaining, 0, dy + lineHeight, Nil, line ++ textAcc)
          else
            val charArea = Rect(
              x = textOp.area.x + dx,
              y = textOp.area.y + dy,
              w = width,
              h = textOp.fontSize
            )
            layout(cs, dx + width, dy, RenderOp.DrawChar(charArea, char, textOp.color) :: lineAcc, textAcc)
      }
    layout(textOp.text.toList, 0, 0, Nil, Nil)
