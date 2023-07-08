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
enum RenderOp:
  case DrawRect(area: Rect, color: Color)
  case DrawText(
      area: Rect,
      color: Color,
      text: String,
      fontSize: Int,
      textArea: Rect,
      horizontalAlignment: TextLayout.HorizontalAlignment,
      verticalAlignment: TextLayout.VerticalAlignment
  )
  case Custom[T](area: Rect, color: Color, data: T)

object RenderOp:

  /** Operation to draw a single character.
    *  Note that this is not part of the RenderOp enum. InterIm components will never generate this operation.
    *
    *  The only way to get it is to call `DrawText#asDrawChars`.
    */
  final case class DrawChar(area: Rect, color: Color, char: Char)

  /** Converts a DrawText operation into a sequence of simpler DrawChar operations.
    *
    * @param charWith function that, given a char, returns its width in pixels
    * @param lineHeight line height to use, in pixels
    */
  extension (textOp: DrawText)
    def asDrawChars(
        charWidth: Char => Int = _ => textOp.fontSize,
        lineHeight: Int = (textOp.fontSize * 1.3).toInt
    ): List[DrawChar] = TextLayout.asDrawChars(textOp, charWidth, lineHeight)

  extension (renderOp: RenderOp)
    def clip(rect: Rect): RenderOp =
      renderOp match
        case dr: DrawRect => dr.copy(area = dr.area & rect)
        case dt: DrawText => dt.copy(area = dt.area & rect)
        case c: Custom[_] => c.copy(area = c.area & rect)
