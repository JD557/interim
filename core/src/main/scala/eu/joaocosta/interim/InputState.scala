package eu.joaocosta.interim

import scala.annotation.tailrec

/** Input State to be used by the components. */
sealed trait InputState:

  /** Current Mouse state */
  def mouseInput: InputState.MouseInput

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

  /** Creates a new InputState.
    *
    * @param mouseX mouse X position, from the left
    * @param mouseY mouse Y position, from the top
    * @param mousePressed whether the mouse is pressed
    * @param keyboardInput
    *   String generated from the keyboard inputs since the last frame. Usually this will be a single character.
    *   A `\u0008` character is interpreted as a backspace.
    */
  def apply(mouseX: Int, mouseY: Int, mouseDown: Boolean, keyboardInput: String): InputState =
    InputState.Current(InputState.MouseInput(mouseX, mouseY, mouseDown), keyboardInput)

  /** Mouse position and button state.
    *
    * @param x mouse X position, from the left
    * @param y mouse Y position, from the top
    * @param isPressed whether the mouse is pressed
    */
  final case class MouseInput(x: Int, y: Int, isPressed: Boolean)

  /** Input state at the current point in time
    *
    * @param mouseInput the current mouse state
    * @param keyboardInput
    *   String generated from the keyboard inputs since the last frame. Usually this will be a single character.
    *   A `\u0008` character is interpreted as a backspace.
    */
  final case class Current(mouseInput: InputState.MouseInput, keyboardInput: String) extends InputState:

    def clip(area: Rect): InputState.Current =
      if (area.isMouseOver(using this)) this
      else this.copy(mouseInput = mouseInput.copy(x = Int.MinValue, y = Int.MinValue))

  /** Input state at the current point in time and in the previous frame
    *
    * @param previousMouseX previous mouse X position, from the left
    * @param previousMouseY previous mouse Y position, from the top
    * @param mouseX mouse X position, from the left
    * @param mouseY mouse Y position, from the top
    * @param mouseDown whether the mouse is pressed
    * @param keyboardInput
    *   String generated from the keyboard inputs since the last frame. Usually this will be a single character.
    *   A `\u0008` character is interpreted as a backspace.
    */
  final case class Historical(
      previousMouseInput: MouseInput,
      mouseInput: MouseInput,
      keyboardInput: String
  ) extends InputState:

    /** If true, then the mouse was released on this frame, performing a click */
    lazy val mouseClicked: Boolean = mouseInput.isPressed == false && previousMouseInput.isPressed == true

    /** How much the mouse moved in the X axis */
    lazy val deltaX: Int =
      if (previousMouseInput.x == Int.MinValue || mouseInput.x == Int.MinValue) 0
      else mouseInput.x - previousMouseInput.x

    /** How much the mouse moved in the Y axis */
    lazy val deltaY: Int =
      if (previousMouseInput.y == Int.MinValue || mouseInput.y == Int.MinValue) 0
      else mouseInput.y - previousMouseInput.y

    def clip(area: Rect): InputState.Historical =
      if (area.isMouseOver(using this)) this
      else this.copy(mouseInput = mouseInput.copy(x = Int.MinValue, y = Int.MinValue))
