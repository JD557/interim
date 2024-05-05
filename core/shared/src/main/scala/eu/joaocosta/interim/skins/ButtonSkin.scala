package eu.joaocosta.interim.skins

import eu.joaocosta.interim.TextLayout._
import eu.joaocosta.interim._
import eu.joaocosta.interim.api.Primitives._

trait ButtonSkin:
  def allocateArea(allocator: LayoutAllocator.AreaAllocator, label: String): Rect
  def buttonArea(area: Rect): Rect
  def renderButton(area: Rect, label: String, itemStatus: UiContext.ItemStatus)(using
      uiContext: UiContext
  ): Unit

object ButtonSkin extends DefaultSkin:

  final case class Default(
      buttonHeight: Int,
      font: Font,
      colorScheme: ColorScheme
  ) extends ButtonSkin:

    def allocateArea(allocator: LayoutAllocator.AreaAllocator, label: String): Rect =
      allocator.allocate(label, font, paddingH = buttonHeight / 2)

    def buttonArea(area: Rect): Rect =
      area.copy(w = area.w, h = area.h - buttonHeight)

    def renderButton(area: Rect, label: String, itemStatus: UiContext.ItemStatus)(using
        uiContext: UiContext
    ): Unit =
      val buttonArea  = this.buttonArea(area)
      val clickedArea = buttonArea.move(dx = 0, dy = buttonHeight)
      itemStatus match
        case UiContext.ItemStatus(false, false, _, _) =>
          rectangle(area.copy(y = buttonArea.y2, h = buttonHeight), colorScheme.primaryShadow)
          rectangle(buttonArea, colorScheme.primary)
        case UiContext.ItemStatus(true, false, _, _) =>
          rectangle(area.copy(y = buttonArea.y2, h = buttonHeight), colorScheme.primary)
          rectangle(buttonArea, colorScheme.primaryHighlight)
        case UiContext.ItemStatus(false, true, _, _) =>
          rectangle(area.copy(y = buttonArea.y2, h = buttonHeight), colorScheme.primary)
          rectangle(buttonArea, colorScheme.primaryHighlight)
        case UiContext.ItemStatus(true, true, _, _) =>
          rectangle(clickedArea, colorScheme.primaryHighlight)
      itemStatus match
        case UiContext.ItemStatus(true, true, _, _) =>
          text(clickedArea, colorScheme.text, label, font, HorizontalAlignment.Center, VerticalAlignment.Center)
        case _ =>
          text(buttonArea, colorScheme.text, label, font, HorizontalAlignment.Center, VerticalAlignment.Center)

  val lightDefault: Default = Default(
    buttonHeight = 3,
    font = Font.default,
    colorScheme = ColorScheme.lightScheme
  )

  val darkDefault: Default = Default(
    buttonHeight = 3,
    font = Font.default,
    colorScheme = ColorScheme.darkScheme
  )
