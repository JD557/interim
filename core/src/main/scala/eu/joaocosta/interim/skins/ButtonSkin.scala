package eu.joaocosta.interim.skins

import eu.joaocosta.interim.TextLayout.*
import eu.joaocosta.interim.*
import eu.joaocosta.interim.api.Primitives.*

trait ButtonSkin:
  def buttonArea(area: Rect): Rect
  def renderButton(area: Rect, text: String, fontSize: Int, itemStatus: UiState.ItemStatus)(implicit
      uiState: UiState
  ): Unit

object ButtonSkin:
  final case class Default(
      shadowDelta: Int = 4,
      clickDelta: Int = 2,
      shadowColor: Color = Color(32, 27, 33),
      textColor: Color = Color(32, 27, 33),
      inactiveColor: Color = Color(37, 199, 238),
      hotColor: Color = Color(123, 228, 255),
      activeColor: Color = Color(123, 228, 255)
  ) extends ButtonSkin:
    def buttonArea(area: Rect): Rect =
      area.copy(w = area.w - shadowDelta, h = area.h - shadowDelta)
    def renderButton(area: Rect, label: String, fontSize: Int, itemStatus: UiState.ItemStatus)(implicit
        uiState: UiState
    ): Unit =
      val buttonArea = this.buttonArea(area)
      rectangle(
        buttonArea.move(dx = shadowDelta, dy = shadowDelta),
        shadowColor
      )
      itemStatus match
        case UiState.ItemStatus(false, false, _) =>
          rectangle(buttonArea, inactiveColor)
          text(buttonArea, textColor, label, fontSize, HorizontalAlignment.Center, VerticalAlignment.Center)
        case UiState.ItemStatus(true, false, _) =>
          rectangle(buttonArea, hotColor)
          text(buttonArea, textColor, label, fontSize, HorizontalAlignment.Center, VerticalAlignment.Center)
        case UiState.ItemStatus(false, true, _) =>
          rectangle(buttonArea, activeColor)
          text(buttonArea, textColor, label, fontSize, HorizontalAlignment.Center, VerticalAlignment.Center)
        case UiState.ItemStatus(true, true, _) =>
          val clickedArea = buttonArea.move(dx = clickDelta, dy = clickDelta)
          rectangle(clickedArea, activeColor)
          text(clickedArea, textColor, label, fontSize, HorizontalAlignment.Center, VerticalAlignment.Center)
