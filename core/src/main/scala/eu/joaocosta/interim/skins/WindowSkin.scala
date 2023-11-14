package eu.joaocosta.interim.skins

import eu.joaocosta.interim.TextLayout.*
import eu.joaocosta.interim.*
import eu.joaocosta.interim.api.Components.*
import eu.joaocosta.interim.api.Primitives.*

trait WindowSkin:
  def titleArea(area: Rect): Rect
  def titleTextArea(area: Rect): Rect
  def panelArea(area: Rect): Rect
  def resizeArea(area: Rect): Rect
  def ensureMinimumArea(area: Rect): Rect
  def renderWindow(area: Rect, title: String)(using uiContext: UiContext): Unit

object WindowSkin extends DefaultSkin:

  final case class Default(
      font: Font,
      border: Int,
      textColor: Color,
      panelColor: Color,
      titleColor: Color,
      borderColor: Color
  ) extends WindowSkin:

    def titleArea(area: Rect): Rect =
      area.copy(h = font.fontSize * 2)

    def titleTextArea(area: Rect): Rect =
      area.copy(h = font.fontSize * 2).shrink(font.fontSize / 2)

    def panelArea(area: Rect): Rect =
      area.copy(y = area.y + font.fontSize * 2, h = area.h - font.fontSize * 2)

    def resizeArea(area: Rect): Rect =
      area.copy(
        x = area.x2 - font.fontSize,
        y = area.y2 - font.fontSize,
        w = font.fontSize,
        h = font.fontSize
      )

    def ensureMinimumArea(area: Rect): Rect =
      area.copy(w = math.max(font.fontSize * 8, area.w), h = math.max(font.fontSize * 8, area.h))

    def renderWindow(area: Rect, title: String)(using uiContext: UiContext): Unit =
      val titleArea = this.titleArea(area)
      val panelArea = this.panelArea(area)
      rectangle(titleArea, titleColor)
      text(titleArea, textColor, title, font, HorizontalAlignment.Center, VerticalAlignment.Center)
      rectangle(panelArea, panelColor)
      rectangleOutline(area, borderColor, border)

  val lightDefault: Default = Default(
    font = Font.default,
    border = 1,
    textColor = ColorScheme.black,
    panelColor = ColorScheme.white,
    titleColor = ColorScheme.lightGray,
    borderColor = ColorScheme.darkGray
  )

  val darkDefault: Default = Default(
    font = Font.default,
    border = 1,
    textColor = ColorScheme.white,
    panelColor = ColorScheme.black,
    titleColor = ColorScheme.darkGray,
    borderColor = ColorScheme.lightGray
  )
