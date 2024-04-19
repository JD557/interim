package eu.joaocosta.interim.skins

import eu.joaocosta.interim._
import eu.joaocosta.interim.api.Primitives._

trait CheckboxSkin:
  def checkboxArea(area: Rect): Rect
  def renderCheckbox(area: Rect, value: Boolean, itemStatus: UiContext.ItemStatus)(using uiContext: UiContext): Unit

object CheckboxSkin extends DefaultSkin:

  final case class Default(
      padding: Int,
      colorScheme: ColorScheme
  ) extends CheckboxSkin:

    def checkboxArea(area: Rect): Rect =
      val smallSide = math.min(area.w, area.h)
      area.copy(w = smallSide, h = smallSide)

    def renderCheckbox(area: Rect, value: Boolean, itemStatus: UiContext.ItemStatus)(using uiContext: UiContext): Unit =
      val checkboxArea = this.checkboxArea(area)
      itemStatus match
        case UiContext.ItemStatus(false, false, _, _) =>
          rectangle(checkboxArea, colorScheme.secondary)
        case UiContext.ItemStatus(true, false, _, _) =>
          rectangle(checkboxArea, colorScheme.secondaryHighlight)
        case UiContext.ItemStatus(_, true, _, _) =>
          rectangle(checkboxArea, colorScheme.primaryHighlight)
      if (value) rectangle(checkboxArea.shrink(padding), colorScheme.icon)

  val lightDefault: Default = Default(
    padding = 2,
    ColorScheme.lightScheme
  )

  val darkDefault: Default = Default(
    padding = 2,
    ColorScheme.darkScheme
  )
