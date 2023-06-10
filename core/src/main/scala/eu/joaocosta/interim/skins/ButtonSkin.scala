package eu.joaocosta.interim.skins

import eu.joaocosta.interim.TextLayout.*
import eu.joaocosta.interim.*
import eu.joaocosta.interim.api.Primitives.*

trait ButtonSkin:
  def buttonArea(area: Rect): Rect
  def renderButton(area: Rect, label: String, itemStatus: UiState.ItemStatus)(implicit
      uiState: UiState
  ): Unit

object ButtonSkin:
  final case class Default(
      shadowDelta: Int,
      clickDelta: Int,
      fontSize: Int,
      shadowColor: Color,
      textColor: Color,
      inactiveColor: Color,
      hotColor: Color,
      activeColor: Color
  ) extends ButtonSkin:
    def buttonArea(area: Rect): Rect =
      area.copy(w = area.w - shadowDelta, h = area.h - shadowDelta)
    def renderButton(area: Rect, label: String, itemStatus: UiState.ItemStatus)(implicit
        uiState: UiState
    ): Unit =
      val buttonArea  = this.buttonArea(area)
      val clickedArea = buttonArea.move(dx = clickDelta, dy = clickDelta)
      rectangle(
        buttonArea.move(dx = shadowDelta, dy = shadowDelta),
        shadowColor
      )
      itemStatus match
        case UiState.ItemStatus(false, false, _) =>
          rectangle(buttonArea, inactiveColor)
        case UiState.ItemStatus(true, false, _) =>
          rectangle(buttonArea, hotColor)
        case UiState.ItemStatus(false, true, _) =>
          rectangle(buttonArea, activeColor)
        case UiState.ItemStatus(true, true, _) =>
          rectangle(clickedArea, activeColor)
      itemStatus match
        case UiState.ItemStatus(true, true, _) =>
          text(clickedArea, textColor, label, fontSize, HorizontalAlignment.Center, VerticalAlignment.Center)
        case _ =>
          text(buttonArea, textColor, label, fontSize, HorizontalAlignment.Center, VerticalAlignment.Center)

  val lightDefault = Default(
    shadowDelta = 4,
    clickDelta = 2,
    fontSize = 8,
    shadowColor = ColorScheme.black,
    textColor = ColorScheme.black,
    inactiveColor = ColorScheme.lightPrimary,
    hotColor = ColorScheme.lightPrimaryHighlight,
    activeColor = ColorScheme.lightPrimaryHighlight
  )
