package eu.joaocosta.interim.skins

import eu.joaocosta.interim._

trait CheckboxSkin:
  def checkboxArea(area: Rect): Rect
  def renderCheckbox(area: Rect, value: Boolean, hot: Boolean, active: Boolean)(implicit uiState: UiState): Unit

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
    def renderCheckbox(area: Rect, value: Boolean, hot: Boolean, active: Boolean)(implicit uiState: UiState): Unit =
      val checkboxArea = this.checkboxArea(area)
      (hot, active) match
        case (false, false) =>
          InterIm.rectangle(checkboxArea, inactiveColor)
        case (true, false) =>
          InterIm.rectangle(checkboxArea, hotColor)
        case (_, true) =>
          InterIm.rectangle(checkboxArea, activeColor)
      if (value)
        InterIm.rectangle(
          checkboxArea.shrink(padding),
          checkColor
        )
