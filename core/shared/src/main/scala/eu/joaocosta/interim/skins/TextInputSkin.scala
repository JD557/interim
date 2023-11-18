package eu.joaocosta.interim.skins

import eu.joaocosta.interim.*
import eu.joaocosta.interim.api.Primitives.*

trait TextInputSkin:
  def textInputArea(area: Rect): Rect
  def renderTextInput(area: Rect, value: String, itemStatus: UiContext.ItemStatus)(using uiContext: UiContext): Unit

object TextInputSkin extends DefaultSkin:

  final case class Default(
      border: Int,
      activeBorder: Int,
      font: Font,
      inactiveColor: Color,
      hotColor: Color,
      activeColor: Color,
      textAreaColor: Color,
      activeTextAreaColor: Color,
      textColor: Color
  ) extends TextInputSkin:

    def textInputArea(area: Rect): Rect = area

    def renderTextInput(area: Rect, value: String, itemStatus: UiContext.ItemStatus)(using uiContext: UiContext): Unit =
      val textInputArea = this.textInputArea(area)
      itemStatus match
        case UiContext.ItemStatus(_, _, true, _) | UiContext.ItemStatus(_, true, _, _) =>
          rectangle(textInputArea, activeTextAreaColor)
          rectangleOutline(area, activeColor, activeBorder)
        case UiContext.ItemStatus(true, _, _, _) =>
          rectangle(textInputArea, textAreaColor)
          rectangleOutline(area, hotColor, border)
        case _ =>
          rectangle(textInputArea, textAreaColor)
          rectangleOutline(area, inactiveColor, border)
      text(
        textInputArea.shrink(activeBorder),
        textColor,
        value,
        font,
        TextLayout.HorizontalAlignment.Left,
        TextLayout.VerticalAlignment.Center
      )

  val lightDefault: Default = Default(
    border = 1,
    activeBorder = 2,
    font = Font.default,
    inactiveColor = ColorScheme.pureGray,
    hotColor = ColorScheme.lightPrimary,
    activeColor = ColorScheme.lightPrimaryHighlight,
    textAreaColor = ColorScheme.lightGray,
    activeTextAreaColor = ColorScheme.white,
    textColor = ColorScheme.black
  )

  val darkDefault: Default = Default(
    border = 1,
    activeBorder = 2,
    font = Font.default,
    inactiveColor = ColorScheme.pureGray,
    hotColor = ColorScheme.darkPrimary,
    activeColor = ColorScheme.darkPrimaryHighlight,
    textAreaColor = ColorScheme.darkGray,
    activeTextAreaColor = ColorScheme.pureGray,
    textColor = ColorScheme.white
  )
