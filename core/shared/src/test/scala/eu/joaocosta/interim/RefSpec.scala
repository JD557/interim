package eu.joaocosta.interim

class RefSpec extends munit.FunSuite:

  test("A Ref value can be correctly set and retrieved with := and get"):
    val x = Ref(1)
    assertEquals(x.get, 1)
    x := 2
    assertEquals(x.get, 2)

  test("Ref values can be modified with modify"):
    val x = Ref(1)

    assertEquals(x.modify(_ + 1).get, 2)
    assertEquals(x.get, 2)

  test("Ref values can be modified with modifyIf"):
    val x = Ref(1)

    assertEquals(x.modifyIf(false)(_ + 1).get, 1)
    assertEquals(x.get, 1)
    assertEquals(x.modifyIf(true)(_ + 1).get, 2)
    assertEquals(x.get, 2)

  test("withRef allows to use a temporary Ref value"):
    val result = Ref.withRef(0): ref =>
      ref.modify(_ + 2)
    assertEquals(result, 2)

  test("withRefs allows to build a case class from temporary Ref values"):
    case class Foo(x: Int, y: String)
    val result = Ref.withRefs(Foo(1, "asd")): (x, y) =>
      x := 2
      y := "dsa"
    assertEquals(result, Foo(2, "dsa"))

  test("asRef allows to use a temporary Ref value"):
    val result = 0.asRef: ref =>
      ref.modify(_ + 2)
    assertEquals(result, 2)

  test("asRefs allows to build a case class from temporary Ref values"):
    case class Foo(x: Int, y: String)
    val result = Foo(1, "asd").asRefs: (x, y) =>
      x := 2
      y := "dsa"
    assertEquals(result, Foo(2, "dsa"))

  test("modifyRefs allows to modify a case class Ref from temporary Ref values"):
    case class Foo(x: Int, y: String)
    val ref = Ref(Foo(1, "asd"))
    ref.modifyRefs: (x, y) =>
      x := 2
      y := "dsa"
    assertEquals(ref.get, Foo(2, "dsa"))
