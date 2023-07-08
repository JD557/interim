package eu.joaocosta.interim.skins

import eu.joaocosta.interim.*
import eu.joaocosta.interim.api.Primitives.*

trait CheckboxSkin:
  def checkboxArea(area: Rect): Rect
  def renderCheckbox(area: Rect, value: Boolean, itemStatus: UiState.ItemStatus)(using uiState: UiState): Unit

object CheckboxSkin extends DefaultSkin:
  final case class Default(
      padding: Int,
      inactiveColor: Color,
      hotColor: Color,
      activeColor: Color,
      checkColor: Color
  ) extends CheckboxSkin:
    def checkboxArea(area: Rect): Rect =
      val smallSide = math.min(area.w, area.h)
      area.copy(w = smallSide, h = smallSide)
    def renderCheckbox(area: Rect, value: Boolean, itemStatus: UiState.ItemStatus)(using uiState: UiState): Unit =
      val checkboxArea = this.checkboxArea(area)
      itemStatus match
        case UiState.ItemStatus(false, false, _) =>
          rectangle(checkboxArea, inactiveColor)
        case UiState.ItemStatus(true, false, _) =>
          rectangle(checkboxArea, hotColor)
        case UiState.ItemStatus(_, true, _) =>
          rectangle(checkboxArea, activeColor)
      if (value)
        rectangle(
          checkboxArea.shrink(padding),
          checkColor
        )

  val lightDefault: Default = Default(
    padding = 2,
    inactiveColor = ColorScheme.lightGray,
    hotColor = ColorScheme.lightPrimary,
    activeColor = ColorScheme.lightPrimaryHighlight,
    checkColor = ColorScheme.black
  )

  val darkDefault: Default = Default(
    padding = 2,
    inactiveColor = ColorScheme.darkGray,
    hotColor = ColorScheme.darkPrimary,
    activeColor = ColorScheme.darkPrimaryHighlight,
    checkColor = ColorScheme.white
  )
