package eu.joaocosta.interim.api

import eu.joaocosta.interim.Rect

class LayoutsSpec extends munit.FunSuite:
  test("grid correctly lays out elements in a grid"):
    val areas = Layouts.grid(Rect(10, 10, 100, 100), numRows = 3, numColumns = 2, padding = 8)(identity)
    val expected =
      Vector(
        Vector(Rect(10, 10, 46, 28), Rect(64, 10, 46, 28)),
        Vector(Rect(10, 46, 46, 28), Rect(64, 46, 46, 28)),
        Vector(Rect(10, 82, 46, 28), Rect(64, 82, 46, 28))
      )
    assertEquals(areas, expected)

  test("rows correctly lays out elements in rows"):
    val areas = Layouts.rows(Rect(10, 10, 100, 100), numRows = 3, padding = 8)(identity)
    val expected =
      Vector(Rect(10, 10, 100, 28), Rect(10, 46, 100, 28), Rect(10, 82, 100, 28))
    assertEquals(areas, expected)

  test("columns correctly lays out elements in columns"):
    val areas = Layouts.columns(Rect(10, 10, 100, 100), numColumns = 3, padding = 8)(identity)
    val expected =
      Vector(Rect(10, 10, 28, 100), Rect(46, 10, 28, 100), Rect(82, 10, 28, 100))
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