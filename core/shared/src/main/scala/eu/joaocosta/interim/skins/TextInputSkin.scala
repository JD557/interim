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
      colorScheme: ColorScheme
  ) extends TextInputSkin:

    def textInputArea(area: Rect): Rect = area

    def renderTextInput(area: Rect, value: String, itemStatus: UiContext.ItemStatus)(using uiContext: UiContext): Unit =
      val textInputArea = this.textInputArea(area)
      itemStatus match
        case UiContext.ItemStatus(_, _, true, _) | UiContext.ItemStatus(_, true, _, _) =>
          rectangle(textInputArea, colorScheme.background)
          rectangleOutline(area, colorScheme.primaryHighlight, activeBorder)
        case UiContext.ItemStatus(true, _, _, _) =>
          rectangle(textInputArea, colorScheme.secondary)
          rectangleOutline(area, colorScheme.primary, border)
        case _ =>
          rectangle(textInputArea, colorScheme.secondary)
          rectangleOutline(area, colorScheme.borderColor, border)
      text(
        textInputArea.shrink(activeBorder),
        colorScheme.text,
        value,
        font,
        TextLayout.HorizontalAlignment.Left,
        TextLayout.VerticalAlignment.Center
      )

  val lightDefault: Default = Default(
    border = 1,
    activeBorder = 2,
    font = Font.default,
    colorScheme = ColorScheme.lightScheme
  )

  val darkDefault: Default = Default(
    border = 1,
    activeBorder = 2,
    font = Font.default,
    colorScheme = ColorScheme.darkScheme
  )
