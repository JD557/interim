package eu.joaocosta.interim.skins

import eu.joaocosta.interim._
import eu.joaocosta.interim.api.LayoutAllocator
import eu.joaocosta.interim.api.Primitives._

trait SliderSkin:
  def allocateArea(allocator: LayoutAllocator): Rect
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

    def allocateArea(allocator: LayoutAllocator): Rect =
      allocator.allocate(Font.default.fontSize, Font.default.fontSize)

    def sliderArea(area: Rect): Rect = area.shrink(padding)

    def renderSlider(area: Rect, min: Int, value: Int, max: Int, itemStatus: UiContext.ItemStatus)(using
        uiContext: UiContext
    ): Unit =
      val sliderArea = this.sliderArea(area)
      val delta      = value - min
      val steps      = (max - min) + 1
      val sliderRect =
        if (area.w > area.h)
          val sliderSize = math.max(minSliderSize, sliderArea.w / steps)
          val maxX =
            (steps + 1) * sliderArea.w / steps - sliderSize // Correction for when the slider hits the min size
          val deltaX = delta * maxX / steps
          Rect(0, 0, sliderSize, sliderArea.h)
            .centerAt(0, sliderArea.centerY)
            .copy(x = sliderArea.x + deltaX)
        else
          val sliderSize = math.max(minSliderSize, sliderArea.h / steps)
          val maxY =
            (steps + 1) * sliderArea.h / steps - sliderSize // Correction for when the slider hits the min size
          val deltaY = delta * maxY / steps
          Rect(0, 0, sliderArea.w, sliderSize)
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
