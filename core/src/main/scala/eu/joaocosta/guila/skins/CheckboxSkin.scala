package eu.joaocosta.guila.skins

import eu.joaocosta.guila._

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
      area
    def renderCheckbox(area: Rect, value: Boolean, hot: Boolean, active: Boolean)(implicit uiState: UiState): Unit =
      val checkboxArea = this.checkboxArea(area)
      (hot, active) match
        case (false, false) =>
          Guila.rectangle(checkboxArea, inactiveColor)
        case (true, false) =>
          Guila.rectangle(checkboxArea, hotColor)
        case (_, true) =>
          Guila.rectangle(checkboxArea, activeColor)
      if (value)
        Guila.rectangle(
          checkboxArea.shrink(padding),
          checkColor
        )
