package eu.joaocosta.interim

import scala.annotation.tailrec

/** Input State to be used by the components. */
sealed trait InputState:

  /** Mouse X position, from the left */
  def mouseX: Int

  /** Mouse Y position, from the top */
  def mouseY: Int

  /** @param mouseDown whether the mouse is pressed */
  def mouseDown: Boolean

  /** String generated from the keyboard inputs since the last frame. Usually this will be a single character.
    * A `\u0008` character is interpreted as a backspace.
    */
  def keyboardInput: String

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

  /** Clips the mouse position to a rectagle. If the mouse is outside of the region, it will be moved to (Int.MinValue, Int.MinValue) */
  def clip(area: Rect): InputState

object InputState:
  /** Creates a new Input State
    *
    * @param mouseX mouse X position, from the left
    * @param mouseY mouse Y position, from the top
    * @param mouseDown whether the body is pressed
    * @param keyboardInput
    *   String generated from the keyboard inputs since the last frame. Usually this will be a single character.
    *   A `\u0008` character is interpreted as a backspace.
    */
  def apply(mouseX: Int, mouseY: Int, mouseDown: Boolean, keyboardInput: String): InputState =
    InputState.Current(mouseX, mouseY, mouseDown, keyboardInput: String)

  /** Input state at the current point in time
    *
    * @param mouseX mouse X position, from the left
    * @param mouseY mouse Y position, from the top
    * @param mouseDown whether the body is pressed
    * @param keyboardInput
    *   String generated from the keyboard inputs since the last frame. Usually this will be a single character.
    *   A `\u0008` character is interpreted as a backspace.
    */
  final case class Current(mouseX: Int, mouseY: Int, mouseDown: Boolean, keyboardInput: String) extends InputState:

    def clip(area: Rect): InputState.Current =
      if (area.isMouseOver(using this)) this
      else this.copy(mouseX = Int.MinValue, mouseY = Int.MinValue)

  /** Input state at the current point in time and in the previous frame
    *
    * @param previousMouseX previous mouse X position, from the left
    * @param previousMouseY previous mouse Y position, from the top
    * @param mouseX mouse X position, from the left
    * @param mouseY mouse Y position, from the top
    * @param mouseDown whether the body is pressed
    * @param keyboardInput
    *   String generated from the keyboard inputs since the last frame. Usually this will be a single character.
    *   A `\u0008` character is interpreted as a backspace.
    */
  final case class Historical(
      previousMouseX: Int,
      previousMouseY: Int,
      mouseX: Int,
      mouseY: Int,
      mouseDown: Boolean,
      keyboardInput: String
  ) extends InputState:

    /** How much the mouse moved in the X axis */
    lazy val deltaX: Int =
      if (previousMouseX == Int.MinValue || mouseX == Int.MinValue) 0
      else mouseX - previousMouseX

    /** How much the mouse moved in the Y axis */
    lazy val deltaY: Int =
      if (previousMouseY == Int.MinValue || mouseY == Int.MinValue) 0
      else mouseY - previousMouseY

    def clip(area: Rect): InputState.Historical =
      if (area.isMouseOver(using this)) this
      else this.copy(mouseX = Int.MinValue, mouseY = Int.MinValue)
