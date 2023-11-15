package eu.joaocosta.interim

import scala.collection.mutable

/** Internal state of the UI.
  *
  * This object keeps the mutable state of the UI and should not be manipulated manually.
  *
  * Instead, it should be created with `new UiContext()` at the start of the application and passed on every frame to
  * [[eu.joaocosta.interim.InterIm.ui]].
  */
final class UiContext private (
    private[interim] var currentZ: Int,
    private[interim] var previousInputState: Option[InputState],
    private[interim] var hotItem: Option[(Int, ItemId)], // Item being hovered by the mouse
    private[interim] var activeItem: Option[ItemId],     // Item being clicked by the mouse
    private[interim] var selectedItem: Option[ItemId],   // Last item clicked
    private[interim] val ops: mutable.TreeMap[Int, mutable.Queue[RenderOp]]
):

  private def registerItem(id: ItemId, area: Rect, passive: Boolean)(using
      inputState: InputState
  ): UiContext.ItemStatus =
    if (area.isMouseOver && hotItem.forall((hotZ, _) => hotZ <= currentZ))
      hotItem = Some(currentZ -> id)
      if (!passive && (activeItem == None || activeItem == Some(id)) && inputState.mouseInput.isPressed)
        activeItem = Some(id)
        selectedItem = Some(id)
    val hot      = hotItem.map(_._2) == Some(id)
    val active   = activeItem == Some(id)
    val selected = selectedItem == Some(id)
    val clicked  = hot && active && inputState.mouseInput.isPressed == false
    UiContext.ItemStatus(hot, active, selected, clicked)

  private[interim] def getOrderedOps(): List[RenderOp] =
    ops.values.toList.flatten

  private[interim] def pushInputState(inputState: InputState): InputState.Historical =
    val history = InputState.Historical(
      previousMouseInput = previousInputState
        .map(_.mouseInput)
        .getOrElse(InputState.MouseInput(None, false)),
      mouseInput = inputState.mouseInput,
      keyboardInput = inputState.keyboardInput
    )
    previousInputState = Some(inputState)
    history

  def this() = this(0, None, None, None, None, new mutable.TreeMap())

  override def clone(): UiContext =
    new UiContext(
      currentZ,
      previousInputState,
      hotItem,
      activeItem,
      selectedItem,
      ops.clone().mapValuesInPlace((_, v) => v.clone())
    )

  def fork(): UiContext =
    new UiContext(currentZ, previousInputState, hotItem, activeItem, selectedItem, new mutable.TreeMap())

  def ++=(that: UiContext): this.type =
    // previousInputState stays the same
    this.hotItem = that.hotItem
    this.activeItem = that.activeItem
    this.selectedItem = that.selectedItem
    that.ops.foreach: (z, ops) =>
      if (this.ops.contains(z)) this.ops(z) ++= that.ops(z)
      else this.ops(z) = that.ops(z)
    this

  def pushRenderOp(op: RenderOp): this.type =
    if (!this.ops.contains(currentZ)) this.ops(currentZ) = new mutable.Queue()
    this.ops(currentZ).addOne(op)
    this

object UiContext:
  /** Status of an item.
    *
    *  @param hot if the mouse is on top of the item
    *  @param active if the mouse clicked the item (and is still pressed down).
    *                This value stays true for one extra frame, so that it's
    *                possible to trigger an action on mouse up (see `clicked`).
    *  @param selected if this was the last element clicked
    *  @param clicked if the mouse clicked this element and was just released.
    */
  final case class ItemStatus(hot: Boolean, active: Boolean, selected: Boolean, clicked: Boolean)

  /** Registers an item on the UI state, taking a certain area.
    *
    * Components register themselves on every frame to update and check their status.
    *
    * Note that this is only required when creating new components.
    *
    * @param id Item ID to register
    * @param area the area of this component
    * @param passive passive items are items such as windows that are never marked as active,
    *                they are just registered to block components under them.
    * @return the item status of the registered component.
    */
  def registerItem(id: ItemId, area: Rect, passive: Boolean = false)(using
      uiContext: UiContext,
      inputState: InputState
  ): UiContext.ItemStatus =
    uiContext.registerItem(id, area, passive)

  /** Applies the operations in a code block at a specified z-index
    *  (higher z-indices show on front of lower z-indices).
    */
  def withZIndex[T](zIndex: Int)(body: (UiContext) ?=> T)(using uiContext: UiContext): T =
    val oldZ = uiContext.currentZ
    uiContext.currentZ = zIndex
    val res = body(using uiContext)
    uiContext.currentZ = oldZ
    res
