package eu.joaocosta.interim.skins

import eu.joaocosta.interim.*
import eu.joaocosta.interim.api.Primitives.*

trait SelectSkin:
  def selectBoxArea(area: Rect): Rect
  def renderSelectBox(area: Rect, value: Int, labels: Vector[String], itemStatus: UiContext.ItemStatus)(using
      uiContext: UiContext
  ): Unit

  def selectOptionArea(area: Rect, value: Int): Rect
  def renderSelectOption(area: Rect, value: Int, labels: Vector[String], itemStatus: UiContext.ItemStatus)(using
      uiContext: UiContext
  ): Unit

object SelectSkin extends DefaultSkin:

  final case class Default(
      border: Int,
      font: Font,
      inactiveColor: Color,
      hotColor: Color,
      activeColor: Color,
      textColor: Color
  ) extends SelectSkin:

    // Select box
    def selectBoxArea(area: Rect): Rect =
      area

    def renderSelectBox(area: Rect, value: Int, labels: Vector[String], itemStatus: UiContext.ItemStatus)(using
        uiContext: UiContext
    ): Unit =
      val selectBoxArea = this.selectBoxArea(area)
      val selectedLabel = labels.applyOrElse(value, _ => "")
      itemStatus match
        case UiContext.ItemStatus(_, _, true) | UiContext.ItemStatus(_, true, _) =>
          rectangle(selectBoxArea, activeColor)
        case UiContext.ItemStatus(true, _, _) =>
          rectangle(selectBoxArea, hotColor)
        case UiContext.ItemStatus(_, _, _) =>
          rectangle(selectBoxArea, inactiveColor)
      text(
        selectBoxArea.shrink(border),
        textColor,
        selectedLabel,
        font,
        TextLayout.HorizontalAlignment.Left,
        TextLayout.VerticalAlignment.Center
      )

    // Select option
    def selectOptionArea(area: Rect, value: Int): Rect =
      area.copy(y = area.y + area.h * (value + 1))

    def renderSelectOption(area: Rect, value: Int, labels: Vector[String], itemStatus: UiContext.ItemStatus)(using
        uiContext: UiContext
    ): Unit =
      val selectOptionArea = this.selectOptionArea(area, value)
      val optionLabel      = labels.applyOrElse(value, _ => "")
      onTop:
        itemStatus match
          case UiContext.ItemStatus(_, _, true) | UiContext.ItemStatus(_, true, _) =>
            rectangle(selectOptionArea, activeColor)
          case UiContext.ItemStatus(true, _, _) =>
            rectangle(selectOptionArea, hotColor)
          case UiContext.ItemStatus(_, _, _) =>
            rectangle(selectOptionArea, inactiveColor)
        text(
          selectOptionArea.shrink(border),
          textColor,
          optionLabel,
          font,
          TextLayout.HorizontalAlignment.Left,
          TextLayout.VerticalAlignment.Center
        )

  val lightDefault: Default = Default(
    border = 2,
    font = Font.default,
    inactiveColor = ColorScheme.lightGray,
    hotColor = ColorScheme.pureGray,
    activeColor = ColorScheme.lightPrimaryHighlight,
    textColor = ColorScheme.black
  )

  val darkDefault: Default = Default(
    border = 2,
    font = Font.default,
    inactiveColor = ColorScheme.darkGray,
    hotColor = ColorScheme.pureGray,
    activeColor = ColorScheme.darkPrimaryHighlight,
    textColor = ColorScheme.white
  )
