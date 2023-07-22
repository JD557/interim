package eu.joaocosta.interim

import scala.annotation.tailrec

object TextLayout:
  enum HorizontalAlignment:
    case Left, Center, Right

  enum VerticalAlignment:
    case Top, Center, Bottom

  private def cumulativeSum(xs: List[Int]): List[Int] = xs match {
    case Nil => Nil
    case _   => xs.tail.scanLeft(xs.head)(_ + _)
  }

  private def cumulativeSum[A](xs: List[A])(f: A => Int): List[(A, Int)] =
    xs.zip(cumulativeSum(xs.map(f)))

  private def getNextLine(str: String, lineSize: Int, charWidth: Char => Int): (String, String) =
    def textSize(str: String): Int = str.iterator.map(charWidth).sum
    if (str.isEmpty) ("", "")
    else
      val (nextFullLine, remainingLines) = str.span(_ != '\n')
      // If the line fits, simply return the line
      if (textSize(nextFullLine) < lineSize) (nextFullLine, remainingLines.drop(1))
      else
        val words                      = nextFullLine.split(" ").map(word => word -> textSize(word)).toList
        val (firstWord, firstWordSize) = words.headOption.getOrElse(("", 0))
        // If the first word is too big, it needs to be broken
        if (firstWordSize > lineSize)
          val (firstPart, secondPart) = cumulativeSum(firstWord.toList)(charWidth).span(_._2 < lineSize)
          (firstPart.mkString(""), secondPart.mkString("") ++ remainingLines)
        else // Otherwise, pick as many words as fit
          val (selectedWords, remainingWords) = cumulativeSum(words)(_._2 + charWidth(' ')).span(_._2 < lineSize)
          (selectedWords.map(_._1._1).mkString(" "), remainingWords.map(_._1._1).mkString(" ") ++ remainingLines)

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
        remaining: String,
        dy: Int,
        textAcc: List[RenderOp.DrawChar]
    ): List[RenderOp.DrawChar] =
      remaining match
        case "" =>
          alignV(
            textAcc,
            textOp.textArea.h,
            textOp.verticalAlignment
          )
        case str =>
          if (dy + textOp.fontSize > textOp.textArea.h) layout("", dy, textAcc) // End here
          else
            val (thisLine, nextLine) = getNextLine(str, textOp.textArea.w, charWidth)
            val ops = cumulativeSum(thisLine.toList)(charWidth).map { case (char, dx) =>
              val width = charWidth(char)
              val charArea = Rect(
                x = textOp.textArea.x + dx - width,
                y = textOp.textArea.y + dy,
                w = width,
                h = textOp.fontSize
              )
              RenderOp.DrawChar(charArea, textOp.color, char)
            }
            layout(nextLine, dy + lineHeight, alignH(ops, textOp.textArea.w, textOp.horizontalAlignment) ++ textAcc)

    layout(textOp.text, 0, Nil).filter(char => (char.area & textOp.area) == char.area)
