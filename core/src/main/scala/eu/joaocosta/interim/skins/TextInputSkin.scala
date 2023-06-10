package eu.joaocosta.interim.skins

import eu.joaocosta.interim.*
import eu.joaocosta.interim.api.Primitives.*

trait TextInputSkin:
  def textInputArea(area: Rect): Rect
  def renderTextInput(area: Rect, value: String, itemStatus: UiState.ItemStatus)(implicit uiState: UiState): Unit

object TextInputSkin:
  final case class Default(
      border: Int = 2,
      padding: Int = 4,
      fontSize: Int = 8,
      inactiveColor: Color = ColorScheme.lightGray,
      hotColor: Color = ColorScheme.lightPrimary,
      activeColor: Color = ColorScheme.lightPrimaryHighlight,
      borderColor: Color = ColorScheme.black,
      textColor: Color = ColorScheme.black
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
