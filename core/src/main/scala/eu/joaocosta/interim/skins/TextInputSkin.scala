package eu.joaocosta.interim.skins

import eu.joaocosta.interim.*
import eu.joaocosta.interim.api.Primitives.*

trait TextInputSkin:
  def textInputArea(area: Rect): Rect
  def renderTextInput(area: Rect, value: String, itemStatus: UiState.ItemStatus)(implicit uiState: UiState): Unit

object TextInputSkin extends DefaultSkin:
  final case class Default(
      border: Int,
      fontSize: Int,
      inactiveColor: Color,
      hotColor: Color,
      activeColor: Color,
      textAreaColor: Color,
      textColor: Color
  ) extends TextInputSkin:
    def textInputArea(area: Rect): Rect =
      area.shrink(border)
    def renderTextInput(area: Rect, value: String, itemStatus: UiState.ItemStatus)(implicit uiState: UiState): Unit =
      val textInputArea = this.textInputArea(area)
      itemStatus match
        case UiState.ItemStatus(_, _, true) | UiState.ItemStatus(_, true, _) =>
          rectangle(area, activeColor)
        case UiState.ItemStatus(true, _, _) =>
          rectangle(area, hotColor)
        case UiState.ItemStatus(_, _, _) =>
          rectangle(area, inactiveColor)
      rectangle(textInputArea, textAreaColor)
      text(
        textInputArea.shrink(border),
        textColor,
        value,
        fontSize,
        TextLayout.HorizontalAlignment.Left,
        TextLayout.VerticalAlignment.Center
      )

  val lightDefault: Default = Default(
    border = 4,
    fontSize = 8,
    inactiveColor = ColorScheme.darkGray,
    hotColor = ColorScheme.lightPrimary,
    activeColor = ColorScheme.lightPrimaryHighlight,
    textAreaColor = ColorScheme.lightGray,
    textColor = ColorScheme.black
  )

  val darkDefault: Default = Default(
    border = 4,
    fontSize = 8,
    inactiveColor = ColorScheme.lightGray,
    hotColor = ColorScheme.darkPrimary,
    activeColor = ColorScheme.darkPrimaryHighlight,
    textAreaColor = ColorScheme.darkGray,
    textColor = ColorScheme.white
  )
