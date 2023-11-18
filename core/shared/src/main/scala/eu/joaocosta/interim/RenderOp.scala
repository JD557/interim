package eu.joaocosta.interim

/** Render operation that needs to be handled by the render backend.
  *
  * There are 3 types of operation:
  *  - DrawRect, to draw a rectangle;
  *  - DrawText, to draw text in an area, with a font size in px and a defined alignment;
  *  - Custom, for advanced use cases not supported by interim out of the box.
  *
  * Every operation has an area and a color, so that a naive implementation can just draw a box if they don't support
  * some operations.
  *
  * For DrawText, the backend is expected to layout the text. However, there's a `asDrawChars` method that applies
  * some naive layout logic and returns simpler [[RenderOp.DrawChar]] operations.
  */
sealed trait RenderOp {
  def area: Rect
  def color: Color

  def clip(rect: Rect): RenderOp
}

object RenderOp:

  /** Operation to draw a rectangle on the screen.
    *
    *  @param area area to render
    *  @param color color of the rectangle
    */
  final case class DrawRect(area: Rect, color: Color) extends RenderOp:
    def clip(rect: Rect): DrawRect = copy(area = area & rect)

  /** Operation to draw text on the screen.
    *
    *  @param area area to render, text outside this area should not be shown
    *  @param color text color
    *  @param text string to render
    *  @param font font size and style
    *  @param textArea area where the text should be layed out
    *  @param horizontalAlignment how the text should be layed out horizontally
    *  @param verticalAlignment how the text should be layed out vertically
    */
  final case class DrawText(
      area: Rect,
      color: Color,
      text: String,
      font: Font,
      textArea: Rect,
      horizontalAlignment: TextLayout.HorizontalAlignment,
      verticalAlignment: TextLayout.VerticalAlignment
  ) extends RenderOp:
    def clip(rect: Rect): DrawText = copy(area = area & rect)

    /** Converts a DrawText operation into a sequence of simpler DrawChar operations.
      *
      * @param lineHeight line height to use, in pixels
      */
    def asDrawChars(
        lineHeight: Int = (font.fontSize * 1.3).toInt
    ): List[DrawChar] = TextLayout.asDrawChars(this, lineHeight)

  /** Operation to draw a custom element on the screen
    *
    *  @param area area to render
    *  @param color fallback color
    *  @param data domain specific data to use when rendering this element
    */
  final case class Custom[T](area: Rect, color: Color, data: T) extends RenderOp:
    def clip(rect: Rect): Custom[T] = copy(area = area & rect)

  /** Operation to draw a single character.
    *  Note that this is not part of the RenderOp enum. InterIm components will never generate this operation.
    *
    *  The only way to get it is to call `DrawText#asDrawChars`.
    */
  final case class DrawChar(area: Rect, color: Color, char: Char)
