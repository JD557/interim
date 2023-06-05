package eu.joaocosta.guila.skins

import eu.joaocosta.guila._

trait ButtonSkin:
  def buttonArea(area: Rect): Rect
  def renderButton(area: Rect, text: String, hot: Boolean, active: Boolean)(implicit uiState: UiState): Unit

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
    def renderButton(area: Rect, text: String, hot: Boolean, active: Boolean)(implicit uiState: UiState): Unit =
      val buttonArea = this.buttonArea(area)
      Guila.rectangle(
        buttonArea.move(dx = shadowDelta, dy = shadowDelta),
        shadowColor
      ) // Shadow
      (hot, active) match
        case (false, false) =>
          Guila.rectangle(buttonArea, inactiveColor)
          Guila.text(buttonArea, text, textColor, true)
        case (true, false) =>
          Guila.rectangle(buttonArea, hotColor)
          Guila.text(buttonArea, text, textColor, true)
        case (false, true) =>
          Guila.rectangle(buttonArea, activeColor)
          Guila.text(buttonArea, text, textColor, true)
        case (true, true) =>
          val clickedArea = buttonArea.move(dx = clickDelta, dy = clickDelta)
          Guila.rectangle(clickedArea, activeColor)
          Guila.text(clickedArea, text, textColor, true)
