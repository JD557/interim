package eu.joaocosta.interim.api

class RefSpec extends munit.FunSuite:
  test("A Ref value can be correctly set and retrieved with := and value"):
    val x = Ref(1)
    assertEquals(x.value, 1)
    x := 2
    assertEquals(x.value, 2)
    x.value = 3
    assertEquals(x.value, 3)

  test("Ref values and raw values can be fetched with Ref.get"):
    val x = Ref(1)
    val y = 1
    assertEquals(Ref.get[Int](x), Ref.get[Int](y))

  test("Ref values and raw values can be set with Ref.set"):
    val x = Ref(1)
    val y = 1

    assertEquals(Ref.set[Int](x, 2), 2)
    assertEquals(Ref.set[Int](y, 2), 2)
    assertEquals(x.value, 2)
    assertEquals(y, 1)

  test("Ref values and raw values can be modified with Ref.modify"):
    val x = Ref(1)
    val y = 1

    assertEquals(Ref.modify[Int](x, _ + 1), 2)
    assertEquals(Ref.modify[Int](y, _ + 1), 2)
    assertEquals(x.value, 2)
    assertEquals(y, 1)

  test("withRef allows to use a temporary Ref value"):
    val result = Ref.withRef(0) { ref =>
      Ref.modify[Int](ref, _ + 2)
    }
    assertEquals(result, 2)

  test("asRef allows to use a temporary Ref value"):
    import Ref.asRef
    val result = 0.asRef { ref =>
      Ref.modify[Int](ref, _ + 2)
    }
    assertEquals(result, 2)
