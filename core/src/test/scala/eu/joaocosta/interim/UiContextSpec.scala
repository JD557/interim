package eu.joaocosta.interim

class UiContextSpec extends munit.FunSuite:
  test("registerItem should not mark an item not under the cursor"):
    given uiContext: UiContext   = new UiContext()
    given inputState: InputState = InputState(0, 0, false, "")
    val itemStatus               = UiContext.registerItem(1, Rect(1, 1, 10, 10))
    assertEquals(itemStatus.hot, false)
    assertEquals(itemStatus.active, false)
    assertEquals(itemStatus.keyboardFocus, false)
    assertEquals(uiContext.hotItem, None)
    assertEquals(uiContext.activeItem, None)
    assertEquals(uiContext.keyboardFocusItem, None)

  test("registerItem should mark an item under the cursor as hot"):
    given uiContext: UiContext   = new UiContext()
    given inputState: InputState = InputState(5, 5, false, "")
    val itemStatus               = UiContext.registerItem(1, Rect(1, 1, 10, 10))
    assertEquals(itemStatus.hot, true)
    assertEquals(itemStatus.active, false)
    assertEquals(itemStatus.keyboardFocus, false)
    assertEquals(uiContext.hotItem, Some(1))
    assertEquals(uiContext.activeItem, None)
    assertEquals(uiContext.keyboardFocusItem, None)

  test("registerItem should mark a clicked item as active and focused"):
    given uiContext: UiContext   = new UiContext()
    given inputState: InputState = InputState(5, 5, true, "")
    val itemStatus               = UiContext.registerItem(1, Rect(1, 1, 10, 10))
    assertEquals(itemStatus.hot, true)
    assertEquals(itemStatus.active, true)
    assertEquals(itemStatus.keyboardFocus, true)
    assertEquals(uiContext.hotItem, Some(1))
    assertEquals(uiContext.activeItem, Some(1))
    assertEquals(uiContext.keyboardFocusItem, Some(1))

  test("registerItem should not override an active item with another one"):
    val uiContext   = new UiContext()
    val inputState1 = InputState(5, 5, true, "")
    UiContext.registerItem(1, Rect(1, 1, 10, 10))(using uiContext, inputState1)
    val inputState2 = InputState(20, 20, true, "")
    UiContext.registerItem(1, Rect(1, 1, 10, 10))(using uiContext, inputState2)
    val itemStatus = UiContext.registerItem(2, Rect(15, 15, 10, 10))(using uiContext, inputState2)
    assertEquals(itemStatus.hot, true)
    assertEquals(itemStatus.active, false)
    assertEquals(itemStatus.keyboardFocus, false)
    assertEquals(uiContext.hotItem, Some(2))
    assertEquals(uiContext.activeItem, Some(1))
    assertEquals(uiContext.keyboardFocusItem, Some(1))

  test("fork should create a new UiContext with no ops, and merge them back with ++="):
    val uiContext: UiContext = new UiContext()
    api.Primitives.rectangle(Rect(0, 0, 1, 1), Color(0, 0, 0))(using uiContext)
    assertEquals(uiContext.getOrderedOps(), List(RenderOp.DrawRect(Rect(0, 0, 1, 1), Color(0, 0, 0))))
    val forked = uiContext.fork()
    assertEquals(forked.getOrderedOps(), Nil)
    api.Primitives.rectangle(Rect(0, 0, 1, 1), Color(1, 2, 3))(using forked)
    assertEquals(uiContext.getOrderedOps(), List(RenderOp.DrawRect(Rect(0, 0, 1, 1), Color(0, 0, 0))))
    assertEquals(forked.getOrderedOps(), List(RenderOp.DrawRect(Rect(0, 0, 1, 1), Color(1, 2, 3))))
    uiContext ++= forked
    assertEquals(
      uiContext.getOrderedOps(),
      List(
        RenderOp.DrawRect(Rect(0, 0, 1, 1), Color(0, 0, 0)),
        RenderOp.DrawRect(Rect(0, 0, 1, 1), Color(1, 2, 3))
      )
    )

  test("operations with a higher z-index should be returned last"):
    given uiContext: UiContext = new UiContext()
    UiContext.withZIndex(1):
      api.Primitives.rectangle(Rect(0, 0, 1, 1), Color(3, 3, 3))
      api.Primitives.rectangle(Rect(0, 0, 1, 1), Color(4, 4, 4))
    UiContext.withZIndex(-1):
      api.Primitives.rectangle(Rect(0, 0, 1, 1), Color(0, 0, 0))
      api.Primitives.rectangle(Rect(0, 0, 1, 1), Color(1, 1, 1))
    api.Primitives.rectangle(Rect(0, 0, 1, 1), Color(2, 2, 2))

    assertEquals(
      uiContext.getOrderedOps(),
      List(
        RenderOp.DrawRect(Rect(0, 0, 1, 1), Color(0, 0, 0)),
        RenderOp.DrawRect(Rect(0, 0, 1, 1), Color(1, 1, 1)),
        RenderOp.DrawRect(Rect(0, 0, 1, 1), Color(2, 2, 2)),
        RenderOp.DrawRect(Rect(0, 0, 1, 1), Color(3, 3, 3)),
        RenderOp.DrawRect(Rect(0, 0, 1, 1), Color(4, 4, 4))
      )
    )
