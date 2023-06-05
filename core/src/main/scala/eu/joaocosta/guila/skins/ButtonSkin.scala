package eu.joaocosta.guila.skins

import eu.joaocosta.guila._

trait ButtonSkin:
  def buttonArea(area: Rect): Rect
  def renderButton(area: Rect, hot: Boolean, active: Boolean)(implicit uiState: UiState): Unit

object ButtonSkin:
  final case class Default(
      shadowDelta: Int = 8,
      clickDelta: Int = 2,
      shadowColor: Color = Color(0, 0, 0),
      inactiveColor: Color = Color(50, 50, 50),
      hotColor: Color = Color(128, 128, 128),
      activeColor: Color = Color(255, 255, 255)
  ) extends ButtonSkin:
    def buttonArea(area: Rect): Rect =
      area.copy(w = area.w - shadowDelta, h = area.h - shadowDelta)
    def renderButton(area: Rect, hot: Boolean, active: Boolean)(implicit uiState: UiState): Unit =
      val buttonArea = this.buttonArea(area)
      Guila.rectangle(
        buttonArea.copy(x = buttonArea.x + shadowDelta, y = buttonArea.y + shadowDelta),
        shadowColor
      ) // Shadow
      (hot, active) match
        case (false, false) =>
          Guila.rectangle(buttonArea, inactiveColor)
        case (true, false) =>
          Guila.rectangle(buttonArea, hotColor)
        case (false, true) =>
          Guila.rectangle(buttonArea, activeColor)
        case (true, true) =>
          Guila.rectangle(buttonArea.copy(x = buttonArea.x + clickDelta, y = buttonArea.y + clickDelta), activeColor)
