package eu.joaocosta.interim.skins

import eu.joaocosta.interim._
import eu.joaocosta.interim.api.Primitives._

trait HandleSkin:
  def allocateArea(allocator: LayoutAllocator.AreaAllocator): Rect

  def moveHandleArea(area: Rect): Rect
  def closeHandleArea(area: Rect): Rect
  def resizeHandleArea(area: Rect): Rect

  def renderMoveHandle(area: Rect, itemStatus: UiContext.ItemStatus)(using uiContext: UiContext): Unit
  def renderCloseHandle(area: Rect, itemStatus: UiContext.ItemStatus)(using uiContext: UiContext): Unit
  def renderResizeHandle(area: Rect, itemStatus: UiContext.ItemStatus)(using uiContext: UiContext): Unit

object HandleSkin extends DefaultSkin:

  final case class Default(colorScheme: ColorScheme) extends HandleSkin:

    def allocateArea(allocator: LayoutAllocator.AreaAllocator): Rect =
      allocator.allocate(Font.default.fontSize, Font.default.fontSize)

    def moveHandleArea(area: Rect): Rect =
      val smallSide = math.min(area.w, area.h)
      area.copy(w = smallSide, h = smallSide)

    def renderMoveHandle(area: Rect, itemStatus: UiContext.ItemStatus)(using uiContext: UiContext): Unit =
      val handleArea = this.moveHandleArea(area)
      val color      = itemStatus match
        case UiContext.ItemStatus(false, false, _, _) => colorScheme.icon
        case UiContext.ItemStatus(true, false, _, _)  => colorScheme.iconHighlight
        case UiContext.ItemStatus(_, true, _, _)      => colorScheme.primaryHighlight
      val lineHeight = handleArea.h / 3
      rectangle(handleArea.copy(h = lineHeight), color)
      rectangle(handleArea.copy(y = handleArea.y + 2 * lineHeight, h = lineHeight), color)

    def closeHandleArea(area: Rect): Rect =
      val smallSide = math.min(area.w, area.h)
      area.copy(x = area.x + area.w - smallSide, w = smallSide, h = smallSide)

    def renderCloseHandle(area: Rect, itemStatus: UiContext.ItemStatus)(using uiContext: UiContext): Unit =
      val handleArea = this.closeHandleArea(area)
      val color      = itemStatus match
        case UiContext.ItemStatus(false, false, _, _) => colorScheme.icon
        case UiContext.ItemStatus(true, false, _, _)  => colorScheme.iconHighlight
        case UiContext.ItemStatus(_, true, _, _)      => colorScheme.primaryHighlight
      rectangle(handleArea, color)

    def resizeHandleArea(area: Rect): Rect =
      val smallSide = math.min(area.w, area.h)
      area.copy(x = area.x2 - smallSide, w = smallSide, h = smallSide)

    def renderResizeHandle(area: Rect, itemStatus: UiContext.ItemStatus)(using uiContext: UiContext): Unit =
      val handleArea = this.resizeHandleArea(area)
      val color      = itemStatus match
        case UiContext.ItemStatus(false, false, _, _) => colorScheme.icon
        case UiContext.ItemStatus(true, false, _, _)  => colorScheme.iconHighlight
        case UiContext.ItemStatus(_, true, _, _)      => colorScheme.primaryHighlight
      val lineSize = handleArea.h / 3
      rectangle(handleArea.move(dx = handleArea.w - lineSize, dy = 0).copy(w = lineSize), color)
      rectangle(handleArea.move(dx = 0, dy = handleArea.h - lineSize).copy(h = lineSize), color)

  val lightDefault: Default = Default(ColorScheme.lightScheme)

  val darkDefault: Default = Default(ColorScheme.darkScheme)
