package eu.joaocosta.interim.skins

import eu.joaocosta.interim.TextLayout.*
import eu.joaocosta.interim.*
import eu.joaocosta.interim.api.Components.*
import eu.joaocosta.interim.api.Primitives.*

trait WindowSkin:
  def titleArea(area: Rect): Rect
  def titleTextArea(area: Rect): Rect
  def panelArea(area: Rect): Rect
  def renderWindow(area: Rect, title: String)(using uiState: UiState): Unit

object WindowSkin extends DefaultSkin:
  final case class Default(
      font: Font,
      textColor: Color,
      panelColor: Color,
      titleColor: Color
  ) extends WindowSkin:
    def titleArea(area: Rect): Rect =
      area.copy(h = font.fontSize * 2)
    def titleTextArea(area: Rect): Rect =
      area.copy(h = font.fontSize * 2).shrink(font.fontSize / 2)
    def panelArea(area: Rect): Rect =
      area.copy(y = area.y + font.fontSize * 2, h = area.h - font.fontSize * 2)
    def renderWindow(area: Rect, title: String)(using uiState: UiState): Unit =
      val titleArea = this.titleArea(area)
      val panelArea = this.panelArea(area)
      rectangle(titleArea, titleColor)
      text(titleArea, textColor, title, font, HorizontalAlignment.Center, VerticalAlignment.Center)
      rectangle(panelArea, panelColor)

  val lightDefault: Default = Default(
    font = Font.default,
    textColor = ColorScheme.black,
    panelColor = ColorScheme.white,
    titleColor = ColorScheme.lightGray
  )

  val darkDefault: Default = Default(
    font = Font.default,
    textColor = ColorScheme.white,
    panelColor = ColorScheme.black,
    titleColor = ColorScheme.darkGray
  )
