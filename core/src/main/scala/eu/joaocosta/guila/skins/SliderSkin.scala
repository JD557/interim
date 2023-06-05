package eu.joaocosta.guila.skins

import eu.joaocosta.guila._

trait SliderSkin:
  def renderSlider(area: Rect, value: Int, max: Int, hot: Boolean, active: Boolean)(implicit
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
    def renderSlider(area: Rect, value: Int, max: Int, hot: Boolean, active: Boolean)(implicit
        uiState: UiState
    ): Unit =
      val smallSide  = math.min(area.w, area.h)
      val largeSide  = math.max(area.w, area.h)
      val sliderSize = smallSide - 2 * padding
      val maxPos     = largeSide - 2 * padding - sliderSize
      val pos        = (maxPos * value) / max
      val (dx, dy)   = if (area.w > area.h) (pos, 0) else (0, pos)
      val sliderRect = Rect(area.x + padding + dx, area.y + padding + dy, sliderSize, sliderSize)
      Guila.rectangle(area, scrollbarColor) // Scrollbar
      (hot, active) match
        case (false, false) =>
          Guila.rectangle(sliderRect, inactiveColor)
        case (true, false) =>
          Guila.rectangle(sliderRect, hotColor)
        case _ =>
          Guila.rectangle(sliderRect, activeColor)
