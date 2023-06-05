package eu.joaocosta.guila.skins

import eu.joaocosta.guila._

trait ButtonSkin:
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
    def renderButton(area: Rect, hot: Boolean, active: Boolean)(implicit uiState: UiState): Unit =
      Guila.rectangle(area.copy(x = area.x + shadowDelta, y = area.y + shadowDelta), shadowColor) // Shadow
      (hot, active) match
        case (false, false) =>
          Guila.rectangle(area, inactiveColor)
        case (true, false) =>
          Guila.rectangle(area, hotColor)
        case (false, true) =>
          Guila.rectangle(area, activeColor)
        case (true, true) =>
          Guila.rectangle(area.copy(x = area.x + clickDelta, y = area.y + clickDelta), activeColor)
