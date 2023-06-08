package eu.joaocosta.interim.skins

import eu.joaocosta.interim.*
import eu.joaocosta.interim.api.Primitives.*

trait CheckboxSkin:
  def checkboxArea(area: Rect): Rect
  def renderCheckbox(area: Rect, value: Boolean, itemStatus: UiState.ItemStatus)(implicit uiState: UiState): Unit

object CheckboxSkin:
  final case class Default(
      padding: Int = 4,
      inactiveColor: Color = Color(213, 212, 207),
      hotColor: Color = Color(37, 199, 238),
      activeColor: Color = Color(123, 228, 255),
      checkColor: Color = Color(32, 27, 33)
  ) extends CheckboxSkin:
    def checkboxArea(area: Rect): Rect =
      val smallSide = math.min(area.w, area.h)
      area.copy(w = smallSide, h = smallSide)
    def renderCheckbox(area: Rect, value: Boolean, itemStatus: UiState.ItemStatus)(implicit uiState: UiState): Unit =
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
