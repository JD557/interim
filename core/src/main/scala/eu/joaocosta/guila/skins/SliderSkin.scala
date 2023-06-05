package eu.joaocosta.guila.skins

import eu.joaocosta.guila._

trait SliderSkin:
  def renderSlider(x: Int, y: Int, w: Int, h: Int, value: Int, max: Int, hot: Boolean, active: Boolean)(implicit
      uiState: UiState
  ): Unit

object SliderSkin:
  final case class Default(
      padding: Int = 8,
      scrollbarColor: Color = Color(50, 50, 50),
      inactiveColor: Color = Color(128, 128, 128),
      hotColor: Color = Color(128, 128, 128),
      activeColor: Color = Color(255, 255, 255)
  ) extends SliderSkin:
    def renderSlider(x: Int, y: Int, w: Int, h: Int, value: Int, max: Int, hot: Boolean, active: Boolean)(implicit
        uiState: UiState
    ): Unit =
      val smallSide  = math.min(w, h)
      val largeSide  = math.max(w, h)
      val sliderSize = smallSide - 2 * padding
      val maxPos     = largeSide - 2 * padding - sliderSize
      val pos        = (maxPos * value) / max
      val (dx, dy)   = if (w > h) (pos, 0) else (0, pos)
      Guila.rectangle(x, y, w, h, scrollbarColor) // Scrollbar
      (hot, active) match
        case (false, false) =>
          Guila.rectangle(x + padding + dx, y + padding + dy, sliderSize, sliderSize, inactiveColor)
        case (true, false) =>
          Guila.rectangle(x + padding + dx, y + padding + dy, sliderSize, sliderSize, hotColor)
        case _ =>
          Guila.rectangle(x + padding + dx, y + padding + dy, sliderSize, sliderSize, activeColor)
