package eu.joaocosta.interim

import scala.annotation.tailrec

/** Input State to be used by the components.
  *
  * @param mouseX mouse X position, from the left
  * @param mouseY mouse Y position, from the top
  * @param mouseDown whether the body is pressed
  * @param keyboardInput
  *   String generated from the keyboard inputs since the last frame. Usually this will be a single character.
  *   A `\u0008` character is interpreted as a backspace.
  */
final case class InputState(mouseX: Int, mouseY: Int, mouseDown: Boolean, keyboardInput: String):

  /** Appends the current keyboard input to an already existing string.
    * A `\u0008` character in the input is interpreted as a backspace.
    */
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
