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

    def sliderArea(area: Rect): Rect = area.shrink(padding)

    def renderSlider(area: Rect, min: Int, value: Int, max: Int, itemStatus: UiContext.ItemStatus)(using
        uiContext: UiContext
    ): Unit =
      val sliderArea = this.sliderArea(area)
      val sliderSize = this.sliderSize(area, min, max)
      val delta      = value - min
      val maxDelta   = max - min
      val sliderRect =
        if (area.w > area.h)
          val sliderFill = sliderArea.h
          val lastX      = math.max(0, (sliderArea.w - sliderSize))
          val deltaX     = if (lastX == 0) 0 else delta * lastX / maxDelta
          Rect(area.x + padding + deltaX, area.y + padding, sliderSize, sliderFill)
        else
          val sliderFill = sliderArea.w
          val lastY      = math.max(0, (sliderArea.h - sliderSize))
          val deltaY     = if (lastY == 0) 0 else delta * lastY / maxDelta
          Rect(area.x + padding, area.y + padding + deltaY, sliderFill, sliderSize)
      rectangle(area, scrollbarColor) // Scrollbar
      itemStatus match
        case UiContext.ItemStatus(false, false, _, _) =>
          rectangle(sliderRect, inactiveColor)
        case UiContext.ItemStatus(true, false, _, _) =>
          rectangle(sliderRect, hotColor)
        case _ =>
          rectangle(sliderRect, activeColor)

  val lightDefault: Default = Default(
    padding = 1,
    minSliderSize = 8,
    scrollbarColor = ColorScheme.lightGray,
    inactiveColor = ColorScheme.lightPrimaryShadow,
    hotColor = ColorScheme.lightPrimary,
    activeColor = ColorScheme.lightPrimaryHighlight
  )

  val darkDefault: Default = Default(
    padding = 1,
    minSliderSize = 8,
    scrollbarColor = ColorScheme.darkGray,
    inactiveColor = ColorScheme.darkPrimaryShadow,
    hotColor = ColorScheme.darkPrimary,
    activeColor = ColorScheme.darkPrimaryHighlight
  )
