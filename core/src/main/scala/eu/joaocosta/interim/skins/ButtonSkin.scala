package eu.joaocosta.interim.skins

import eu.joaocosta.interim._

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
      InterIm.rectangle(
        buttonArea.move(dx = shadowDelta, dy = shadowDelta),
        shadowColor
      ) // Shadow
      itemStatus match
        case UiState.ItemStatus(false, false) =>
          InterIm.rectangle(buttonArea, inactiveColor)
          InterIm.text(buttonArea, label, fontSize, textColor, true)
        case UiState.ItemStatus(true, false) =>
          InterIm.rectangle(buttonArea, hotColor)
          InterIm.text(buttonArea, label, fontSize, textColor, true)
        case UiState.ItemStatus(false, true) =>
          InterIm.rectangle(buttonArea, activeColor)
          InterIm.text(buttonArea, label, fontSize, textColor, true)
        case UiState.ItemStatus(true, true) =>
          val clickedArea = buttonArea.move(dx = clickDelta, dy = clickDelta)
          InterIm.rectangle(clickedArea, activeColor)
          InterIm.text(clickedArea, label, fontSize, textColor, true)
