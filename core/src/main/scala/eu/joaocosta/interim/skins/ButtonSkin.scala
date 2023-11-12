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
      shadowDelta: Int,
      clickDelta: Int,
      font: Font,
      shadowColor: Color,
      textColor: Color,
      inactiveColor: Color,
      hotColor: Color,
      activeColor: Color
  ) extends ButtonSkin:

    def buttonArea(area: Rect): Rect =
      area.copy(w = area.w - math.max(shadowDelta, clickDelta), h = area.h - math.max(shadowDelta, clickDelta))

    def renderButton(area: Rect, label: String, itemStatus: UiContext.ItemStatus)(using
        uiContext: UiContext
    ): Unit =
      val buttonArea  = this.buttonArea(area)
      val clickedArea = buttonArea.move(dx = clickDelta, dy = clickDelta)
      rectangle(
        buttonArea.move(dx = shadowDelta, dy = shadowDelta),
        shadowColor
      )
      itemStatus match
        case UiContext.ItemStatus(false, false, _, _) =>
          rectangle(buttonArea, inactiveColor)
        case UiContext.ItemStatus(true, false, _, _) =>
          rectangle(buttonArea, hotColor)
        case UiContext.ItemStatus(false, true, _, _) =>
          rectangle(buttonArea, activeColor)
        case UiContext.ItemStatus(true, true, _, _) =>
          rectangle(clickedArea, activeColor)
      itemStatus match
        case UiContext.ItemStatus(true, true, _, _) =>
          text(clickedArea, textColor, label, font, HorizontalAlignment.Center, VerticalAlignment.Center)
        case _ =>
          text(buttonArea, textColor, label, font, HorizontalAlignment.Center, VerticalAlignment.Center)

  val lightDefault: Default = Default(
    shadowDelta = 2,
    clickDelta = 1,
    font = Font.default,
    shadowColor = ColorScheme.darkGray,
    textColor = ColorScheme.black,
    inactiveColor = ColorScheme.lightPrimary,
    hotColor = ColorScheme.lightPrimaryHighlight,
    activeColor = ColorScheme.lightPrimaryHighlight
  )

  val darkDefault: Default = Default(
    shadowDelta = 0,
    clickDelta = 2,
    font = Font.default,
    shadowColor = ColorScheme.black,
    textColor = ColorScheme.white,
    inactiveColor = ColorScheme.darkPrimary,
    hotColor = ColorScheme.darkPrimaryHighlight,
    activeColor = ColorScheme.darkPrimaryHighlight
  )
