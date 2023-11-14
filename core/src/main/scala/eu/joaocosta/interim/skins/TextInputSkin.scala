package eu.joaocosta.interim.skins

import eu.joaocosta.interim.*
import eu.joaocosta.interim.api.Primitives.*

trait TextInputSkin:
  def textInputArea(area: Rect): Rect
  def renderTextInput(area: Rect, value: String, itemStatus: UiContext.ItemStatus)(using uiContext: UiContext): Unit

object TextInputSkin extends DefaultSkin:

  final case class Default(
      border: Int,
      font: Font,
      inactiveColor: Color,
      hotColor: Color,
      activeColor: Color,
      textAreaColor: Color,
      textColor: Color
  ) extends TextInputSkin:

    def textInputArea(area: Rect): Rect =
      area.shrink(border)

    def renderTextInput(area: Rect, value: String, itemStatus: UiContext.ItemStatus)(using uiContext: UiContext): Unit =
      val textInputArea = this.textInputArea(area)
      itemStatus match
        case UiContext.ItemStatus(_, _, true, _) | UiContext.ItemStatus(_, true, _, _) =>
          rectangleOutline(area, activeColor, border)
        case UiContext.ItemStatus(true, _, _, _) =>
          rectangleOutline(area, hotColor, border)
        case _ =>
          rectangleOutline(area, inactiveColor, border)
      rectangle(textInputArea, textAreaColor)
      text(
        textInputArea.shrink(border),
        textColor,
        value,
        font,
        TextLayout.HorizontalAlignment.Left,
        TextLayout.VerticalAlignment.Center
      )

  val lightDefault: Default = Default(
    border = 1,
    font = Font.default,
    inactiveColor = ColorScheme.darkGray,
    hotColor = ColorScheme.lightPrimary,
    activeColor = ColorScheme.lightPrimaryHighlight,
    textAreaColor = ColorScheme.lightGray,
    textColor = ColorScheme.black
  )

  val darkDefault: Default = Default(
    border = 1,
    font = Font.default,
    inactiveColor = ColorScheme.lightGray,
    hotColor = ColorScheme.darkPrimary,
    activeColor = ColorScheme.darkPrimaryHighlight,
    textAreaColor = ColorScheme.darkGray,
    textColor = ColorScheme.white
  )
