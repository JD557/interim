package eu.joaocosta.interim.skins

import eu.joaocosta.interim.TextLayout.*
import eu.joaocosta.interim.*
import eu.joaocosta.interim.api.Components.*
import eu.joaocosta.interim.api.Primitives.*

trait WindowSkin:
  def titleArea(area: Rect): Rect
  def titleTextArea(area: Rect): Rect
  def panelArea(area: Rect): Rect
  def renderWindow(area: Rect, title: String)(implicit uiState: UiState): Unit

object WindowSkin:
  final case class Default(
      fontSize: Int = 8,
      textColor: Color = Color(32, 27, 33),
      panelColor: Color = Color(246, 247, 251),
      titleColor: Color = Color(213, 212, 207)
  ) extends WindowSkin:
    def titleArea(area: Rect): Rect =
      area.copy(h = fontSize * 2)
    def titleTextArea(area: Rect): Rect =
      area.copy(h = fontSize * 2).shrink(fontSize / 2)
    def panelArea(area: Rect): Rect =
      area.copy(y = area.y + fontSize * 2, h = area.h - fontSize * 2)
    def renderWindow(area: Rect, title: String)(implicit uiState: UiState): Unit =
      val titleArea = this.titleArea(area)
      val panelArea = this.panelArea(area)
      rectangle(titleArea, titleColor)
      text(titleArea, textColor, title, fontSize, HorizontalAlignment.Center, VerticalAlignment.Center)
      rectangle(panelArea, panelColor)
