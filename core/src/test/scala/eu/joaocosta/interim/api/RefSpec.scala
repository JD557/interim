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

  // Braces needed due to https://github.com/scalameta/scalafmt/issues/3597
  test("withRefs allows to build a case class from temporary Ref value") {
    case class Foo(x: Int, y: String)
    val result = Ref.withRefs(Foo(1, "asd")) { (x, y) =>
      x := 2
      y := "dsa"
    }
    assertEquals(result, Foo(2, "dsa"))
  }

  test("asRef allows to use a temporary Ref value"):
    import Ref.asRef
    val result = 0.asRef { ref =>
      Ref.modify[Int](ref, _ + 2)
    }
    assertEquals(result, 2)

  test("asRefs allows to build a case class from temporary Ref value"):
    import Ref.asRefs
    case class Foo(x: Int, y: String)
    val result = Foo(1, "asd").asRefs { (x, y) =>
      x := 2
      y := "dsa"
    }
    assertEquals(result, Foo(2, "dsa"))
