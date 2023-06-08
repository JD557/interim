package eu.joaocosta.interim

import scala.annotation.tailrec

final case class InputState(mouseX: Int, mouseY: Int, mouseDown: Boolean, keyboardInput: String):
  def appendKeyboardInput(str: String): String =
    if (keyboardInput.isEmpty) str
    else
      val fullString = str + keyboardInput
      val processedString =
        if (fullString.size >= 2)
          fullString.iterator
            .sliding(2)
            .flatMap {
              case Seq(_, '\u0008') => ""
              case Seq(x, _)        => x.toString
              case seq              => seq.mkString
            }
            .mkString + fullString.lastOption.mkString
        else fullString
      processedString
        .filterNot(Character.isISOControl)
        .mkString
