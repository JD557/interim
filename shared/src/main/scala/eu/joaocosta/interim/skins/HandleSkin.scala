package eu.joaocosta.interim.skins

import eu.joaocosta.interim.*
import eu.joaocosta.interim.api.Primitives.*

trait HandleSkin:
  def moveHandleArea(area: Rect): Rect
  def closeHandleArea(area: Rect): Rect
  def resizeHandleArea(area: Rect): Rect

  def renderMoveHandle(area: Rect, itemStatus: UiContext.ItemStatus)(using uiContext: UiContext): Unit
  def renderCloseHandle(area: Rect, itemStatus: UiContext.ItemStatus)(using uiContext: UiContext): Unit
  def renderResizeHandle(area: Rect, itemStatus: UiContext.ItemStatus)(using uiContext: UiContext): Unit

object HandleSkin extends DefaultSkin:

  final case class Default(
      inactiveColor: Color,
      hotColor: Color,
      activeColor: Color
  ) extends HandleSkin:

    def moveHandleArea(area: Rect): Rect =
      val smallSide = math.min(area.w, area.h)
      area.copy(w = smallSide, h = smallSide)

    def renderMoveHandle(area: Rect, itemStatus: UiContext.ItemStatus)(using uiContext: UiContext): Unit =
      val handleArea = this.moveHandleArea(area)
      val color = itemStatus match
        case UiContext.ItemStatus(false, false, _, _) => inactiveColor
        case UiContext.ItemStatus(true, false, _, _)  => hotColor
        case UiContext.ItemStatus(_, true, _, _)      => activeColor
      val lineHeight = handleArea.h / 3
      rectangle(handleArea.copy(h = lineHeight), color)
      rectangle(handleArea.copy(y = handleArea.y + 2 * lineHeight, h = lineHeight), color)

    def closeHandleArea(area: Rect): Rect =
      val smallSide = math.min(area.w, area.h)
      area.copy(x = area.x + area.w - smallSide, w = smallSide, h = smallSide)

    def renderCloseHandle(area: Rect, itemStatus: UiContext.ItemStatus)(using uiContext: UiContext): Unit =
      val handleArea = this.closeHandleArea(area)
      val color = itemStatus match
        case UiContext.ItemStatus(false, false, _, _) => inactiveColor
        case UiContext.ItemStatus(true, false, _, _)  => hotColor
        case UiContext.ItemStatus(_, true, _, _)      => activeColor
      rectangle(handleArea, color)

    def resizeHandleArea(area: Rect): Rect =
      val smallSide = math.min(area.w, area.h)
      area.copy(x = area.x2 - smallSide, w = smallSide, h = smallSide)

    def renderResizeHandle(area: Rect, itemStatus: UiContext.ItemStatus)(using uiContext: UiContext): Unit =
      val handleArea = this.resizeHandleArea(area)
      val color = itemStatus match
        case UiContext.ItemStatus(false, false, _, _) => inactiveColor
        case UiContext.ItemStatus(true, false, _, _)  => hotColor
        case UiContext.ItemStatus(_, true, _, _)      => activeColor
      val lineSize = handleArea.h / 3
      rectangle(handleArea.move(dx = handleArea.w - lineSize, dy = 0).copy(w = lineSize), color)
      rectangle(handleArea.move(dx = 0, dy = handleArea.h - lineSize).copy(h = lineSize), color)

  val lightDefault: Default = Default(
    inactiveColor = ColorScheme.black,
    hotColor = ColorScheme.pureGray,
    activeColor = ColorScheme.lightPrimaryHighlight
  )

  val darkDefault: Default = Default(
    inactiveColor = ColorScheme.lightGray,
    hotColor = ColorScheme.white,
    activeColor = ColorScheme.darkPrimaryHighlight
  )