package eu.joaocosta.interim

import scala.collection.mutable

class UiStateSpec extends munit.FunSuite {
  test("registerItem should not mark an item not under the cursor") {
    implicit val uiState    = new UiState()
    implicit val inputState = InputState(0, 0, false, "")
    val itemStatus          = UiState.registerItem(1, Rect(1, 1, 10, 10))
    assertEquals(itemStatus.hot, false)
    assertEquals(itemStatus.active, false)
    assertEquals(itemStatus.keyboardFocus, false)
    assertEquals(uiState.hotItem, None)
    assertEquals(uiState.activeItem, None)
    assertEquals(uiState.keyboardFocusItem, None)
  }

  test("registerItem should mark an item under the cursor as hot") {
    implicit val uiState    = new UiState()
    implicit val inputState = InputState(5, 5, false, "")
    val itemStatus          = UiState.registerItem(1, Rect(1, 1, 10, 10))
    assertEquals(itemStatus.hot, true)
    assertEquals(itemStatus.active, false)
    assertEquals(itemStatus.keyboardFocus, false)
    assertEquals(uiState.hotItem, Some(1))
    assertEquals(uiState.activeItem, None)
    assertEquals(uiState.keyboardFocusItem, None)
  }

  test("registerItem should mark a clicked item as active and focused") {
    implicit val uiState    = new UiState()
    implicit val inputState = InputState(5, 5, true, "")
    val itemStatus          = UiState.registerItem(1, Rect(1, 1, 10, 10))
    assertEquals(itemStatus.hot, true)
    assertEquals(itemStatus.active, true)
    assertEquals(itemStatus.keyboardFocus, true)
    assertEquals(uiState.hotItem, Some(1))
    assertEquals(uiState.activeItem, Some(1))
    assertEquals(uiState.keyboardFocusItem, Some(1))
  }

  test("registerItem should not override an active item with another one") {
    val uiState     = new UiState()
    val inputState1 = InputState(5, 5, true, "")
    UiState.registerItem(1, Rect(1, 1, 10, 10))(uiState, inputState1)
    val inputState2 = InputState(20, 20, true, "")
    UiState.registerItem(1, Rect(1, 1, 10, 10))(uiState, inputState2)
    val itemStatus = UiState.registerItem(2, Rect(15, 15, 10, 10))(uiState, inputState2)
    assertEquals(itemStatus.hot, true)
    assertEquals(itemStatus.active, false)
    assertEquals(itemStatus.keyboardFocus, false)
    assertEquals(uiState.hotItem, Some(2))
    assertEquals(uiState.activeItem, Some(1))
    assertEquals(uiState.keyboardFocusItem, Some(1))
  }
}