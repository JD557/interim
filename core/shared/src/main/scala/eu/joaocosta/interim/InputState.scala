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

  /** Clips the mouse position to a rectagle. If the mouse is outside of the region, the position is set to None */
  def clip(area: Rect): InputState

object InputState:

  /** Creates a new InputState.
    *
    * @param mousePosition optional mouse (x, y) position, from the top-left
    * @param mousePressed whether the mouse is pressed
    * @param keyboardInput
    *   String generated from the keyboard inputs since the last frame. Usually this will be a single character.
    *   A `\u0008` character is interpreted as a backspace.
    */
  def apply(mousePosition: Option[(Int, Int)], mouseDown: Boolean, keyboardInput: String): InputState =
    InputState.Current(InputState.MouseInput(mousePosition, mouseDown), keyboardInput)

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
    InputState.Current(InputState.MouseInput(Some((mouseX, mouseY)), mouseDown), keyboardInput)

  /** Creates a new InputState with an unknown mouse position.
    *
    * @param mousePressed whether the mouse is pressed
    * @param keyboardInput
    *   String generated from the keyboard inputs since the last frame. Usually this will be a single character.
    *   A `\u0008` character is interpreted as a backspace.
    */
  def apply(mouseDown: Boolean, keyboardInput: String): InputState =
    InputState.Current(InputState.MouseInput(None, mouseDown), keyboardInput)

  /** Mouse position and button state.
    *
    * @param position mouse position in a (x, y) tuple. None if the mouse is off-screen.
    * @param isPressed whether the mouse is pressed
    */
  final case class MouseInput(position: Option[(Int, Int)], isPressed: Boolean):
    def x = position.map(_._1)
    def y = position.map(_._2)

  object MouseInput:
    /** Mouse position and button state.
      *
      * @param x mouse position from the left side
      * @param y mouse position from the top
      * @param isPressed whether the mouse is pressed
      */
    def apply(x: Int, y: Int, isPressed: Boolean): MouseInput =
      MouseInput(position = Some((x, y)), isPressed = isPressed)

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
      else this.copy(mouseInput = mouseInput.copy(position = None))

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
      mouseInput.x.zip(previousMouseInput.x).fold(0)((curr, prev) => curr - prev)

    /** How much the mouse moved in the Y axis */
    lazy val deltaY: Int =
      mouseInput.y.zip(previousMouseInput.y).fold(0)((curr, prev) => curr - prev)

    def clip(area: Rect): InputState.Historical =
      if (area.isMouseOver(using this)) this
      else this.copy(mouseInput = mouseInput.copy(position = None))
