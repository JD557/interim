package eu.joaocosta.interim.skins

import eu.joaocosta.interim.*
import eu.joaocosta.interim.api.Primitives.*

trait SliderSkin:
  def sliderSize: Int
  def sliderArea(area: Rect): Rect
  def renderSlider(area: Rect, min: Int, value: Int, max: Int, itemStatus: UiState.ItemStatus)(implicit
      uiState: UiState
  ): Unit

object SliderSkin:
  final case class Default(
      padding: Int,
      sliderSize: Int,
      scrollbarColor: Color,
      inactiveColor: Color,
      hotColor: Color,
      activeColor: Color
  ) extends SliderSkin:
    def sliderArea(area: Rect): Rect =
      Rect(area.x + padding, area.y + padding, area.w - 2 * padding, area.h - 2 * padding)
    def renderSlider(area: Rect, min: Int, value: Int, max: Int, itemStatus: UiState.ItemStatus)(implicit
        uiState: UiState
    ): Unit =
      val sliderArea = this.sliderArea(area)
      val sliderRect =
        if (area.w > area.h)
          val sliderFill = area.h - 2 * padding
          val pos        = (value - min) * (sliderArea.w - sliderSize) / (max - min)
          Rect(area.x + padding + pos, area.y + padding, sliderSize, sliderFill)
        else
          val sliderFill = area.w - 2 * padding
          val pos        = (value - min) * (sliderArea.h - sliderSize) / (max - min)
          Rect(area.x + padding, area.y + padding + pos, sliderFill, sliderSize)
      rectangle(area, scrollbarColor) // Scrollbar
      itemStatus match
        case UiState.ItemStatus(false, false, _) =>
          rectangle(sliderRect, inactiveColor)
        case UiState.ItemStatus(true, false, _) =>
          rectangle(sliderRect, hotColor)
        case _ =>
          rectangle(sliderRect, activeColor)

  val lightDefault = Default(
    padding = 8,
    sliderSize = 8,
    scrollbarColor = ColorScheme.lightGray,
    inactiveColor = ColorScheme.lightPrimary,
    hotColor = ColorScheme.lightPrimaryHighlight,
    activeColor = ColorScheme.lightPrimaryHighlight
  )
