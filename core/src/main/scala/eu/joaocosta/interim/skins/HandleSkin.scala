package eu.joaocosta.interim.skins

import eu.joaocosta.interim.*
import eu.joaocosta.interim.api.Primitives.*

trait HandleSkin:
  def moveHandleArea(area: Rect): Rect
  def closeHandleArea(area: Rect): Rect
  def renderMoveHandle(area: Rect, value: Rect, itemStatus: UiContext.ItemStatus)(using uiContext: UiContext): Unit
  def renderCloseHandle(area: Rect, itemStatus: UiContext.ItemStatus)(using uiContext: UiContext): Unit

object HandleSkin extends DefaultSkin:
  final case class Default(
      inactiveColor: Color,
      hotColor: Color,
      activeColor: Color
  ) extends HandleSkin:
    def moveHandleArea(area: Rect): Rect =
      val smallSide = math.min(area.w, area.h)
      area.copy(w = smallSide, h = smallSide)

    def renderMoveHandle(area: Rect, value: Rect, itemStatus: UiContext.ItemStatus)(using uiContext: UiContext): Unit =
      val handleArea = this.moveHandleArea(area)
      val color = itemStatus match
        case UiContext.ItemStatus(false, false, _) => inactiveColor
        case UiContext.ItemStatus(true, false, _)  => hotColor
        case UiContext.ItemStatus(_, true, _)      => activeColor
      val lineHeight = handleArea.h / 3
      rectangle(handleArea.copy(h = lineHeight), color)
      rectangle(handleArea.copy(y = handleArea.y + 2 * lineHeight, h = lineHeight), color)

    def closeHandleArea(area: Rect): Rect =
      val smallSide = math.min(area.w, area.h)
      area.copy(x = area.x + area.w - smallSide, w = smallSide, h = smallSide)

    def renderCloseHandle(area: Rect, itemStatus: UiContext.ItemStatus)(using uiContext: UiContext): Unit =
      val handleArea = this.closeHandleArea(area)
      val color = itemStatus match
        case UiContext.ItemStatus(false, false, _) => inactiveColor
        case UiContext.ItemStatus(true, false, _)  => hotColor
        case UiContext.ItemStatus(_, true, _)      => activeColor
      rectangle(handleArea, color)

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
