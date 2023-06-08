package eu.joaocosta.interim

import scala.annotation.tailrec

class InputStateSpec extends munit.FunSuite:
  test("appendKeyboardInput should preserve the original string"):
    val result = InputState(0, 0, false, "").appendKeyboardInput("test")
    assertEquals(result, "test")

  test("appendKeyboardInput should append both strings"):
    val result = InputState(0, 0, false, " foo").appendKeyboardInput("test")
    assertEquals(result, "test foo")

  test("appendKeyboardInput should delete characters from the keyboard string"):
    val result = InputState(0, 0, false, " f\u0008oo").appendKeyboardInput("test")
    assertEquals(result, "test oo")

  test("appendKeyboardInput should delete characters from the original string"):
    val result = InputState(0, 0, false, "\u0008oo").appendKeyboardInput("test")
    assertEquals(result, "tesoo")

  test("appendKeyboardInput should handle overdeletion"):
    val result = InputState(0, 0, false, "\u0008oo").appendKeyboardInput("test")
    assertEquals(result, "tesoo")
