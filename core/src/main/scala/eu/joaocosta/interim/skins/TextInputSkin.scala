package eu.joaocosta.interim.skins

import eu.joaocosta.interim.*
import eu.joaocosta.interim.api.Primitives.*

trait TextInputSkin:
  def textInputArea(area: Rect): Rect
  def renderTextInput(area: Rect, value: String, itemStatus: UiState.ItemStatus)(implicit uiState: UiState): Unit

object TextInputSkin:
  final case class Default(
      border: Int,
      padding: Int,
      fontSize: Int,
      inactiveColor: Color,
      hotColor: Color,
      activeColor: Color,
      borderColor: Color,
      textColor: Color
  ) extends TextInputSkin:
    def textInputArea(area: Rect): Rect =
      area.shrink(padding)
    def renderTextInput(area: Rect, value: String, itemStatus: UiState.ItemStatus)(implicit uiState: UiState): Unit =
      val textInputArea = this.textInputArea(area)
      rectangle(area, borderColor)
      itemStatus match
        case UiState.ItemStatus(_, _, true) | UiState.ItemStatus(_, true, _) =>
          rectangle(textInputArea, activeColor)
        case UiState.ItemStatus(true, _, _) =>
          rectangle(textInputArea, hotColor)
        case UiState.ItemStatus(_, _, _) =>
          rectangle(textInputArea, inactiveColor)
      text(
        textInputArea.shrink(padding),
        textColor,
        value,
        fontSize,
        TextLayout.HorizontalAlignment.Left,
        TextLayout.VerticalAlignment.Center
      )

  val lightDefault = Default(
    border = 2,
    padding = 4,
    fontSize = 8,
    inactiveColor = ColorScheme.lightGray,
    hotColor = ColorScheme.lightPrimary,
    activeColor = ColorScheme.lightPrimaryHighlight,
    borderColor = ColorScheme.black,
    textColor = ColorScheme.black
  )
