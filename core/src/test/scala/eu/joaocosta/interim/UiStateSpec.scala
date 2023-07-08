package eu.joaocosta.interim

class UiStateSpec extends munit.FunSuite:
  test("registerItem should not mark an item not under the cursor"):
    given uiState: UiState       = new UiState()
    given inputState: InputState = InputState(0, 0, false, "")
    val itemStatus               = UiState.registerItem(1, Rect(1, 1, 10, 10))
    assertEquals(itemStatus.hot, false)
    assertEquals(itemStatus.active, false)
    assertEquals(itemStatus.keyboardFocus, false)
    assertEquals(uiState.hotItem, None)
    assertEquals(uiState.activeItem, None)
    assertEquals(uiState.keyboardFocusItem, None)

  test("registerItem should mark an item under the cursor as hot"):
    given uiState: UiState       = new UiState()
    given inputState: InputState = InputState(5, 5, false, "")
    val itemStatus               = UiState.registerItem(1, Rect(1, 1, 10, 10))
    assertEquals(itemStatus.hot, true)
    assertEquals(itemStatus.active, false)
    assertEquals(itemStatus.keyboardFocus, false)
    assertEquals(uiState.hotItem, Some(1))
    assertEquals(uiState.activeItem, None)
    assertEquals(uiState.keyboardFocusItem, None)

  test("registerItem should mark a clicked item as active and focused"):
    given uiState: UiState       = new UiState()
    given inputState: InputState = InputState(5, 5, true, "")
    val itemStatus               = UiState.registerItem(1, Rect(1, 1, 10, 10))
    assertEquals(itemStatus.hot, true)
    assertEquals(itemStatus.active, true)
    assertEquals(itemStatus.keyboardFocus, true)
    assertEquals(uiState.hotItem, Some(1))
    assertEquals(uiState.activeItem, Some(1))
    assertEquals(uiState.keyboardFocusItem, Some(1))

  test("registerItem should not override an active item with another one"):
    val uiState     = new UiState()
    val inputState1 = InputState(5, 5, true, "")
    UiState.registerItem(1, Rect(1, 1, 10, 10))(using uiState, inputState1)
    val inputState2 = InputState(20, 20, true, "")
    UiState.registerItem(1, Rect(1, 1, 10, 10))(using uiState, inputState2)
    val itemStatus = UiState.registerItem(2, Rect(15, 15, 10, 10))(using uiState, inputState2)
    assertEquals(itemStatus.hot, true)
    assertEquals(itemStatus.active, false)
    assertEquals(itemStatus.keyboardFocus, false)
    assertEquals(uiState.hotItem, Some(2))
    assertEquals(uiState.activeItem, Some(1))
    assertEquals(uiState.keyboardFocusItem, Some(1))

  test("fork should create a new UiState with no ops, and merge them back with ++="):
    val uiState: UiState = new UiState()
    api.Primitives.rectangle(Rect(0, 0, 1, 1), Color(0, 0, 0))(using uiState)
    assertEquals(uiState.ops.toList, List(RenderOp.DrawRect(Rect(0, 0, 1, 1), Color(0, 0, 0))))
    val forked = uiState.fork()
    assertEquals(forked.ops.toList, Nil)
    api.Primitives.rectangle(Rect(0, 0, 1, 1), Color(1, 2, 3))(using forked)
    assertEquals(uiState.ops.toList, List(RenderOp.DrawRect(Rect(0, 0, 1, 1), Color(0, 0, 0))))
    assertEquals(forked.ops.toList, List(RenderOp.DrawRect(Rect(0, 0, 1, 1), Color(1, 2, 3))))
    uiState ++= forked
    assertEquals(
      uiState.ops.toList,
      List(
        RenderOp.DrawRect(Rect(0, 0, 1, 1), Color(0, 0, 0)),
        RenderOp.DrawRect(Rect(0, 0, 1, 1), Color(1, 2, 3))
      )
    )
