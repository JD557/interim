package eu.joaocosta.interim.skins

import eu.joaocosta.interim._

trait SliderSkin:
  def sliderSize: Int
  def sliderArea(area: Rect): Rect
  def renderSlider(area: Rect, value: Int, max: Int, hot: Boolean, active: Boolean)(implicit
      uiState: UiState
  ): Unit

object SliderSkin:
  final case class Default(
      padding: Int = 8,
      val sliderSize: Int = 8,
      scrollbarColor: Color = Color(213, 212, 207),
      inactiveColor: Color = Color(37, 199, 238),
      hotColor: Color = Color(123, 228, 255),
      activeColor: Color = Color(123, 228, 255)
  ) extends SliderSkin:
    def sliderArea(area: Rect): Rect =
      Rect(area.x + padding, area.y + padding, area.w - 2 * padding, area.h - 2 * padding)
    def renderSlider(area: Rect, value: Int, max: Int, hot: Boolean, active: Boolean)(implicit
        uiState: UiState
    ): Unit =
      val sliderArea = this.sliderArea(area)
      val sliderRect =
        if (area.w > area.h)
          val sliderFill = area.h - 2 * padding
          val pos        = value * (sliderArea.w - sliderSize) / max
          Rect(area.x + padding + pos, area.y + padding, sliderSize, sliderFill)
        else
          val sliderFill = area.w - 2 * padding
          val pos        = value * (sliderArea.h - sliderSize) / max
          Rect(area.x + padding, area.y + padding + pos, sliderFill, sliderSize)
      InterIm.rectangle(area, scrollbarColor) // Scrollbar
      (hot, active) match
        case (false, false) =>
          InterIm.rectangle(sliderRect, inactiveColor)
        case (true, false) =>
          InterIm.rectangle(sliderRect, hotColor)
        case _ =>
          InterIm.rectangle(sliderRect, activeColor)
