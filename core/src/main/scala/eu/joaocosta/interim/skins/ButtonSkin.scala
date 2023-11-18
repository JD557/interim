package eu.joaocosta.interim.skins

import eu.joaocosta.interim.TextLayout.*
import eu.joaocosta.interim.*
import eu.joaocosta.interim.api.Primitives.*

trait ButtonSkin:
  def buttonArea(area: Rect): Rect
  def renderButton(area: Rect, label: String, itemStatus: UiContext.ItemStatus)(using
      uiContext: UiContext
  ): Unit

object ButtonSkin extends DefaultSkin:

  final case class Default(
      buttonHeight: Int,
      font: Font,
      shadowColor: Color,
      textColor: Color,
      inactiveColor: Color,
      hotColor: Color,
      activeColor: Color
  ) extends ButtonSkin:

    def buttonArea(area: Rect): Rect =
      area.copy(w = area.w, h = area.h - buttonHeight)

    def renderButton(area: Rect, label: String, itemStatus: UiContext.ItemStatus)(using
        uiContext: UiContext
    ): Unit =
      val buttonArea  = this.buttonArea(area)
      val clickedArea = buttonArea.move(dx = 0, dy = buttonHeight)
      itemStatus match
        case UiContext.ItemStatus(false, false, _, _) =>
          rectangle(area.copy(y = buttonArea.y2, h = buttonHeight), shadowColor)
          rectangle(buttonArea, inactiveColor)
        case UiContext.ItemStatus(true, false, _, _) =>
          rectangle(area.copy(y = buttonArea.y2, h = buttonHeight), inactiveColor)
          rectangle(buttonArea, hotColor)
        case UiContext.ItemStatus(false, true, _, _) =>
          rectangle(area.copy(y = buttonArea.y2, h = buttonHeight), inactiveColor)
          rectangle(buttonArea, activeColor)
        case UiContext.ItemStatus(true, true, _, _) =>
          rectangle(clickedArea, activeColor)
      itemStatus match
        case UiContext.ItemStatus(true, true, _, _) =>
          text(clickedArea, textColor, label, font, HorizontalAlignment.Center, VerticalAlignment.Center)
        case _ =>
          text(buttonArea, textColor, label, font, HorizontalAlignment.Center, VerticalAlignment.Center)

  val lightDefault: Default = Default(
    buttonHeight = 3,
    font = Font.default,
    shadowColor = ColorScheme.lightPrimaryShadow,
    textColor = ColorScheme.black,
    inactiveColor = ColorScheme.lightPrimary,
    hotColor = ColorScheme.lightPrimaryHighlight,
    activeColor = ColorScheme.lightPrimaryHighlight
  )

  val darkDefault: Default = Default(
    buttonHeight = 3,
    font = Font.default,
    shadowColor = ColorScheme.darkPrimaryShadow,
    textColor = ColorScheme.white,
    inactiveColor = ColorScheme.darkPrimary,
    hotColor = ColorScheme.darkPrimaryHighlight,
    activeColor = ColorScheme.darkPrimaryHighlight
  )
