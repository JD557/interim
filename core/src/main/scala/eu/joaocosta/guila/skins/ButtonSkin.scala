package eu.joaocosta.guila.skins

import eu.joaocosta.guila._

trait ButtonSkin:
  def renderButton(x: Int, y: Int, w: Int, h: Int, hot: Boolean, active: Boolean)(implicit uiState: UiState): Unit

object ButtonSkin:
  final case class Default(
      shadowDelta: Int = 8,
      clickDelta: Int = 2,
      shadowColor: Color = Color(0, 0, 0),
      inactiveColor: Color = Color(50, 50, 50),
      hotColor: Color = Color(128, 128, 128),
      activeColor: Color = Color(255, 255, 255)
  ) extends ButtonSkin:
    def renderButton(x: Int, y: Int, w: Int, h: Int, hot: Boolean, active: Boolean)(implicit uiState: UiState): Unit =
      Guila.rectangle(x + shadowDelta, y + shadowDelta, w, h, shadowColor) // Shadow
      (hot, active) match {
        case (false, false) =>
          Guila.rectangle(x, y, w, h, inactiveColor)
        case (true, false) =>
          Guila.rectangle(x, y, w, h, hotColor)
        case (false, true) =>
          Guila.rectangle(x, y, w, h, activeColor)
        case (true, true) =>
          Guila.rectangle(x + clickDelta, y + clickDelta, w, h, activeColor)
      }
