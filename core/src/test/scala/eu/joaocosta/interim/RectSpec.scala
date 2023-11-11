package eu.joaocosta.interim

class RectSpec extends munit.FunSuite:

  test("compute helper positions"):
    val rect = Rect(10, 15, 10, 20)
    assertEquals(rect.x1, 10)
    assertEquals(rect.x2, 20)
    assertEquals(rect.y1, 15)
    assertEquals(rect.y2, 35)
    assertEquals(rect.centerX, 15)
    assertEquals(rect.centerY, 25)

  test("isMouseOver detects collisions with the mouse"):
    val rect = Rect(10, 10, 10, 10)
    assertEquals(rect.isMouseOver(using InputState(0, 0, false, "")), false)
    assertEquals(rect.isMouseOver(using InputState(15, 15, false, "")), true)
    assertEquals(rect.isMouseOver(using InputState(30, 30, false, "")), false)

  test("shrink and grow the rectangle"):
    val rect = Rect(10, 10, 10, 10)
    assertEquals(rect.shrink(2), Rect(12, 12, 6, 6))
    assertEquals(rect.grow(2), Rect(8, 8, 14, 14))

  test("merge expands two rects when there's a gap"):
    val rect1 = Rect(10, 10, 10, 10)
    val rect2 = Rect(30, 10, 10, 10)
    assertEquals(rect1 ++ rect2, Rect(10, 10, 30, 10))
    assertEquals(rect2 ++ rect1, Rect(10, 10, 30, 10))

  test("merge expands two rects when they intersect"):
    val rect1 = Rect(10, 10, 10, 10)
    val rect2 = Rect(15, 10, 10, 10)
    assertEquals(rect1 ++ rect2, Rect(10, 10, 15, 10))
    assertEquals(rect2 ++ rect1, Rect(10, 10, 15, 10))

  test("intersect returns an empty rect when there's a gap"):
    val rect1 = Rect(10, 10, 10, 10)
    val rect2 = Rect(30, 10, 10, 10)
    assertEquals((rect1 & rect2).isEmpty, true)
    assertEquals((rect2 & rect1).isEmpty, true)

  test("intersect shrinks two rects when they intersect"):
    val rect1 = Rect(10, 10, 10, 10)
    val rect2 = Rect(15, 10, 10, 10)
    assertEquals(rect1 & rect2, Rect(15, 10, 5, 10))
    assertEquals(rect2 & rect1, Rect(15, 10, 5, 10))
