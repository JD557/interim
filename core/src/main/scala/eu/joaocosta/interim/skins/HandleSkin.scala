package eu.joaocosta.interim.skins

import eu.joaocosta.interim.*
import eu.joaocosta.interim.api.Primitives.*

trait HandleSkin:
  def handleArea(area: Rect): Rect
  def renderHandle(area: Rect, value: Rect, itemStatus: UiState.ItemStatus)(implicit uiState: UiState): Unit

object HandleSkin extends DefaultSkin:
  final case class Default(
      inactiveColor: Color,
      hotColor: Color,
      activeColor: Color
  ) extends HandleSkin:
    def handleArea(area: Rect): Rect =
      val smallSide = math.min(area.w, area.h)
      area.copy(w = smallSide, h = smallSide)
    def renderHandle(area: Rect, value: Rect, itemStatus: UiState.ItemStatus)(implicit uiState: UiState): Unit =
      val handleArea = this.handleArea(area)
      itemStatus match
        case UiState.ItemStatus(false, false, _) =>
          rectangle(handleArea, inactiveColor)
        case UiState.ItemStatus(true, false, _) =>
          rectangle(handleArea, hotColor)
        case UiState.ItemStatus(_, true, _) =>
          rectangle(handleArea, activeColor)

  val lightDefault: Default = Default(
    inactiveColor = ColorScheme.black,
    hotColor = ColorScheme.lightPrimary,
    activeColor = ColorScheme.lightPrimaryHighlight
  )

  val darkDefault: Default = Default(
    inactiveColor = ColorScheme.white,
    hotColor = ColorScheme.darkPrimary,
    activeColor = ColorScheme.darkPrimaryHighlight
  )
