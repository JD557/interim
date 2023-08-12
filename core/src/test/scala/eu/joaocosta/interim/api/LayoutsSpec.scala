package eu.joaocosta.interim.api

import eu.joaocosta.interim.{Color, InputState, Rect, RenderOp, UiContext}

class LayoutsSpec extends munit.FunSuite:
  test("clip correctly clips render ops"):
    given uiContext: UiContext   = new UiContext()
    given inputState: InputState = InputState(0, 0, false, "")
    Layouts.clip(Rect(10, 10, 10, 10)):
      Primitives.rectangle(Rect(0, 0, 15, 15), Color(0, 0, 0))
    assertEquals(uiContext.getOrderedOps(), List(RenderOp.DrawRect(Rect(10, 10, 5, 5), Color(0, 0, 0))))

  test("clip ignores input outside the clip area"):
    given uiContext: UiContext   = new UiContext()
    given inputState: InputState = InputState(5, 5, false, "")
    val itemStatus =
      Layouts.clip(Rect(10, 10, 10, 10)):
        UiContext.registerItem(1, Rect(0, 0, 15, 15))
    assertEquals(itemStatus.hot, false)

  test("clip considers input inside the clip area"):
    given uiContext: UiContext   = new UiContext()
    given inputState: InputState = InputState(12, 12, false, "")
    val itemStatus =
      Layouts.clip(Rect(10, 10, 10, 10)):
        UiContext.registerItem(1, Rect(0, 0, 15, 15))
    assertEquals(itemStatus.hot, true)

  test("grid correctly lays out elements in a grid"):
    val areas = Layouts.grid(Rect(10, 10, 100, 100), numRows = 3, numColumns = 2, padding = 8)(identity)
    val expected =
      Vector(
        Vector(Rect(10, 10, 46, 28), Rect(64, 10, 46, 28)),
        Vector(Rect(10, 46, 46, 28), Rect(64, 46, 46, 28)),
        Vector(Rect(10, 82, 46, 28), Rect(64, 82, 46, 28))
      )
    assertEquals(areas, expected)

  test("grid returns nothing for an empty grid"):
    val areas    = Layouts.grid(Rect(10, 10, 100, 100), numRows = 0, numColumns = 0, padding = 8)(identity)
    val expected = Vector.empty
    assertEquals(areas, expected)

  test("rows correctly lays out elements in rows"):
    val areas = Layouts.rows(Rect(10, 10, 100, 100), numRows = 3, padding = 8)(identity)
    val expected =
      Vector(Rect(10, 10, 100, 28), Rect(10, 46, 100, 28), Rect(10, 82, 100, 28))
    assertEquals(areas, expected)

  test("rows returns nothing for 0 rows"):
    val areas    = Layouts.rows(Rect(10, 10, 100, 100), numRows = 0, padding = 8)(identity)
    val expected = Vector.empty
    assertEquals(areas, expected)

  test("columns correctly lays out elements in columns"):
    val areas = Layouts.columns(Rect(10, 10, 100, 100), numColumns = 3, padding = 8)(identity)
    val expected =
      Vector(Rect(10, 10, 28, 100), Rect(46, 10, 28, 100), Rect(82, 10, 28, 100))
    assertEquals(areas, expected)

  test("columns returns nothing for 0 columns"):
    val areas    = Layouts.columns(Rect(10, 10, 100, 100), numColumns = 0, padding = 8)(identity)
    val expected = Vector.empty
    assertEquals(areas, expected)

  test("dynamicRows correctly lays out elements in rows"):
    val areas = Layouts.dynamicRows(Rect(10, 10, 100, 100), padding = 8) { nextRow =>
      Vector(nextRow(16), nextRow(-32), nextRow(Int.MaxValue))
    }
    val expected =
      Vector(Rect(10, 10, 100, 16), Rect(10, 78, 100, 32), Rect(10, 34, 100, 36))
    assertEquals(areas, expected)

  test("dynamicColumns correctly lays out elements in columns"):
    val areas = Layouts.dynamicColumns(Rect(10, 10, 100, 100), padding = 8) { nextColumn =>
      Vector(nextColumn(16), nextColumn(-32), nextColumn(Int.MaxValue))
    }
    val expected =
      Vector(Rect(10, 10, 16, 100), Rect(78, 10, 32, 100), Rect(34, 10, 36, 100))
    assertEquals(areas, expected)
