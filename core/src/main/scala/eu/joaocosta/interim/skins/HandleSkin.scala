package eu.joaocosta.interim.skins

import eu.joaocosta.interim.*
import eu.joaocosta.interim.api.Primitives.*

trait HandleSkin:
  def handleArea(area: Rect): Rect
  def renderHandle(area: Rect, value: Rect, itemStatus: UiState.ItemStatus)(using uiState: UiState): Unit

object HandleSkin extends DefaultSkin:
  final case class Default(
      inactiveColor: Color,
      hotColor: Color,
      activeColor: Color
  ) extends HandleSkin:
    def handleArea(area: Rect): Rect =
      val smallSide = math.min(area.w, area.h)
      area.copy(w = smallSide, h = smallSide)
    def renderHandle(area: Rect, value: Rect, itemStatus: UiState.ItemStatus)(using uiState: UiState): Unit =
      val handleArea = this.handleArea(area)
      val color = itemStatus match
        case UiState.ItemStatus(false, false, _) => inactiveColor
        case UiState.ItemStatus(true, false, _)  => hotColor
        case UiState.ItemStatus(_, true, _)      => activeColor
      val lineHeight = handleArea.h / 3
      rectangle(handleArea.copy(h = lineHeight), color)
      rectangle(handleArea.copy(y = handleArea.y + 2 * lineHeight, h = lineHeight), color)

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
