package eu.joaocosta.interim.skins

import eu.joaocosta.interim.*
import eu.joaocosta.interim.api.Primitives.*

trait SliderSkin:
  def sliderArea(area: Rect): Rect
  def renderSlider(area: Rect, min: Int, value: Int, max: Int, itemStatus: UiContext.ItemStatus)(using
      uiContext: UiContext
  ): Unit

object SliderSkin extends DefaultSkin:

  final case class Default(
      padding: Int,
      minSliderSize: Int,
      colorScheme: ColorScheme
  ) extends SliderSkin:

    def sliderArea(area: Rect): Rect = area.shrink(padding)

    def renderSlider(area: Rect, min: Int, value: Int, max: Int, itemStatus: UiContext.ItemStatus)(using
        uiContext: UiContext
    ): Unit =
      val sliderArea = this.sliderArea(area)
      val delta      = value - min
      val steps      = (max - min) + 1
      val sliderRect =
        if (area.w > area.h)
          val deltaX = delta * sliderArea.w / steps
          Rect(0, 0, math.max(minSliderSize, sliderArea.w / steps), sliderArea.h)
            .centerAt(0, sliderArea.centerY)
            .copy(x = sliderArea.x + deltaX)
        else
          val deltaY = delta * sliderArea.h / steps
          Rect(0, 0, sliderArea.w, math.max(minSliderSize, sliderArea.h / steps))
            .centerAt(sliderArea.centerX, 0)
            .copy(y = sliderArea.y + deltaY)
      rectangle(area, colorScheme.secondary)
      itemStatus match
        case UiContext.ItemStatus(false, false, _, _) =>
          rectangle(sliderRect, colorScheme.primaryShadow)
        case UiContext.ItemStatus(true, false, _, _) =>
          rectangle(sliderRect, colorScheme.primary)
        case _ =>
          rectangle(sliderRect, colorScheme.primaryHighlight)

  val lightDefault: Default = Default(
    padding = 1,
    minSliderSize = 8,
    ColorScheme.lightScheme
  )

  val darkDefault: Default = Default(
    padding = 1,
    minSliderSize = 8,
    ColorScheme.darkScheme
  )
