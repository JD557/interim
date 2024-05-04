package eu.joaocosta.interim.skins

import eu.joaocosta.interim._
import eu.joaocosta.interim.api.Primitives._

trait SelectSkin:
  def allocateArea(allocator: LayoutAllocator, labels: Vector[String]): Rect

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
      padding: Int,
      font: Font,
      colorScheme: ColorScheme
  ) extends SelectSkin:

    def allocateArea(allocator: LayoutAllocator, labels: Vector[String]): Rect =
      val largestLabel = labels.maxByOption(_.size).getOrElse("")
      allocator.allocate(largestLabel, font, paddingW = padding, paddingH = padding)

    // Select box
    def selectBoxArea(area: Rect): Rect =
      area

    def renderSelectBox(area: Rect, value: Int, labels: Vector[String], itemStatus: UiContext.ItemStatus)(using
        uiContext: UiContext
    ): Unit =
      val selectBoxArea = this.selectBoxArea(area)
      val selectedLabel = labels.applyOrElse(value, _ => "")
      itemStatus match
        case UiContext.ItemStatus(_, _, true, _) | UiContext.ItemStatus(_, true, _, _) =>
          rectangle(selectBoxArea, colorScheme.primaryHighlight)
        case UiContext.ItemStatus(true, _, _, _) =>
          rectangle(selectBoxArea, colorScheme.secondaryHighlight)
        case UiContext.ItemStatus(_, _, _, _) =>
          rectangle(selectBoxArea, colorScheme.secondary)
      text(
        selectBoxArea.shrink(padding),
        colorScheme.text,
        selectedLabel,
        font,
        HorizontalAlignment.Left,
        VerticalAlignment.Center
      )

    // Select option
    def selectOptionArea(area: Rect, value: Int): Rect =
      area.copy(y = area.y + area.h * (value + 1))

    def renderSelectOption(area: Rect, value: Int, labels: Vector[String], itemStatus: UiContext.ItemStatus)(using
        uiContext: UiContext
    ): Unit =
      val selectOptionArea = this.selectOptionArea(area, value)
      val optionLabel      = labels.applyOrElse(value, _ => "")
      itemStatus match
        case UiContext.ItemStatus(_, _, true, _) | UiContext.ItemStatus(_, true, _, _) =>
          rectangle(selectOptionArea, colorScheme.primaryHighlight)
        case UiContext.ItemStatus(true, _, _, _) =>
          rectangle(selectOptionArea, colorScheme.secondaryHighlight)
        case _ =>
          rectangle(selectOptionArea, colorScheme.secondary)
      text(
        selectOptionArea.shrink(padding),
        colorScheme.text,
        optionLabel,
        font,
        HorizontalAlignment.Left,
        VerticalAlignment.Center
      )

  val lightDefault: Default = Default(
    padding = 2,
    font = Font.default,
    colorScheme = ColorScheme.lightScheme
  )

  val darkDefault: Default = Default(
    padding = 2,
    font = Font.default,
    colorScheme = ColorScheme.darkScheme
  )
