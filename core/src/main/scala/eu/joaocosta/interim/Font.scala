package eu.joaocosta.interim

/** A description of a font.
  *
  * @param name font name
  * @param fontSize font height in pixels
  * @param charWidth the width of each character in pixels
  */
final case class Font(name: String, fontSize: Int, charWidth: Char => Int)

object Font:
  /** A description of a font.
    *
    * All chars are assumed to be square.
    * @param name font name
    * @param fontSize font width and height in pixels
    */
  def apply(name: String, fontSize: Int): Font = Font(name, fontSize, _ => fontSize)

  /** The default font */
  val default = Font("default", 8)
