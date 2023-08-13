package eu.joaocosta.interim.skins

import eu.joaocosta.interim.*
import eu.joaocosta.interim.api.Primitives.*

trait SliderSkin:
  def sliderSize(area: Rect, min: Int, max: Int): Int
  def sliderArea(area: Rect): Rect
  def renderSlider(area: Rect, min: Int, value: Int, max: Int, itemStatus: UiContext.ItemStatus)(using
      uiContext: UiContext
  ): Unit

object SliderSkin extends DefaultSkin:

  final case class Default(
      padding: Int,
      minSliderSize: Int,
      scrollbarColor: Color,
      inactiveColor: Color,
      hotColor: Color,
      activeColor: Color
  ) extends SliderSkin:

    def sliderSize(area: Rect, min: Int, max: Int): Int =
      val steps = (max - min) + 1
      math.max(minSliderSize, math.max(area.w, area.h) / steps)

    def sliderArea(area: Rect): Rect =
      Rect(area.x + padding, area.y + padding, area.w - 2 * padding, area.h - 2 * padding)

    def renderSlider(area: Rect, min: Int, value: Int, max: Int, itemStatus: UiContext.ItemStatus)(using
        uiContext: UiContext
    ): Unit =
      val sliderArea = this.sliderArea(area)
      val sliderSize = this.sliderSize(area, min, max)
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
        case UiContext.ItemStatus(false, false, _) =>
          rectangle(sliderRect, inactiveColor)
        case UiContext.ItemStatus(true, false, _) =>
          rectangle(sliderRect, hotColor)
        case _ =>
          rectangle(sliderRect, activeColor)

  val lightDefault: Default = Default(
    padding = 1,
    minSliderSize = 8,
    scrollbarColor = ColorScheme.lightGray,
    inactiveColor = ColorScheme.darkGray,
    hotColor = ColorScheme.lightPrimary,
    activeColor = ColorScheme.lightPrimaryHighlight
  )

  val darkDefault: Default = Default(
    padding = 1,
    minSliderSize = 8,
    scrollbarColor = ColorScheme.darkGray,
    inactiveColor = ColorScheme.lightGray,
    hotColor = ColorScheme.darkPrimary,
    activeColor = ColorScheme.darkPrimaryHighlight
  )
