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
    private[interim] var currentItemState: UiContext.ItemState,
    private[interim] var scratchItemState: UiContext.ItemState,
    private[interim] val ops: mutable.TreeMap[Int, mutable.Queue[RenderOp]]
):

  private def getItemStatus(id: ItemId)(using inputState: InputState): UiContext.ItemStatus =
    currentItemState.getItemStatus(id)

  private def getScratchItemStatus(id: ItemId)(using inputState: InputState): UiContext.ItemStatus =
    scratchItemState.getItemStatus(id)

  private def registerItem(id: ItemId, area: Rect, passive: Boolean)(using
      inputState: InputState
  ): UiContext.ItemStatus =
    if (area.isMouseOver && scratchItemState.hotItem.forall((hotZ, _) => hotZ <= currentZ))
      scratchItemState.hotItem = Some(currentZ -> id)
      if (inputState.mouseInput.isPressed)
        if (passive && currentItemState.activeItem == None)
          scratchItemState.activeItem = None
          scratchItemState.selectedItem = None
        else if (!passive && currentItemState.activeItem.forall(_ == id))
          scratchItemState.activeItem = Some(id)
          scratchItemState.selectedItem = Some(id)
    getItemStatus(id)

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

  private[interim] def commit(): this.type =
    currentItemState = scratchItemState.clone()
    this

  def this() = this(0, None, UiContext.ItemState(), UiContext.ItemState(), new mutable.TreeMap())

  override def clone(): UiContext =
    new UiContext(
      currentZ,
      previousInputState,
      currentItemState.clone(),
      scratchItemState.clone(),
      ops.clone().mapValuesInPlace((_, v) => v.clone())
    )

  def fork(): UiContext =
    new UiContext(currentZ, previousInputState, currentItemState, scratchItemState, new mutable.TreeMap())

  def ++=(that: UiContext): this.type =
    // previousInputState stays the same
    this.scratchItemState = that.scratchItemState.clone()
    that.ops.foreach: (z, ops) =>
      if (this.ops.contains(z)) this.ops(z) ++= that.ops(z)
      else this.ops(z) = that.ops(z)
    this

  def pushRenderOp(op: RenderOp): this.type =
    if (!this.ops.contains(currentZ)) this.ops(currentZ) = new mutable.Queue()
    this.ops(currentZ).addOne(op)
    this

object UiContext:
  private[interim] class ItemState(
      var hotItem: Option[(Int, ItemId)] = None, // Item being hovered by the mouse
      var activeItem: Option[ItemId] = None,     // Item being clicked by the mouse
      var selectedItem: Option[ItemId] = None    // Last item clicked
  ):
    def getItemStatus(id: ItemId)(using inputState: InputState): UiContext.ItemStatus =
      val hot      = hotItem.map(_._2) == Some(id)
      val active   = activeItem == Some(id)
      val selected = selectedItem == Some(id)
      val clicked  = hot && active && inputState.mouseInput.isPressed == false
      UiContext.ItemStatus(hot, active, selected, clicked)

    override def clone(): ItemState = new ItemState(hotItem, activeItem, selectedItem)

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
    * This is only required when creating new components. If you are using the premade components
    * you do not need to call this.
    *
    * Also of note is that this method returns the status from computed in the previous iteration,
    * as that's the only consistent information.
    * If you need the status as it's being computed, check [[getScratchItemStatus]].
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

  /** Checks the status of a component from the previous UI computation without registering it.
    *
    *  This method can be used if one needs to check the status of an item in the previous iteration
    *  without registering it.
    * @param id Item ID to register
    * @return the item status of the component.
    */
  def getItemStatus(id: ItemId)(using
      uiContext: UiContext,
      inputState: InputState
  ): UiContext.ItemStatus =
    uiContext.getItemStatus(id)

  /** Checks the status of a component at the current point in the UI computation without registering it.
    *
    * This method can be used if one needs to check the status of an item in the middle of the current iteration.
    *
    * This is can return inconsistent state, and is only recommended for unit tests or debugging.
    * @param id Item ID to register
    * @return the item status of the component.
    */
  def getScratchItemStatus(id: ItemId)(using
      uiContext: UiContext,
      inputState: InputState
  ): UiContext.ItemStatus =
    uiContext.getScratchItemStatus(id)

  /** Applies the operations in a code block at a specified z-index
    *  (higher z-indices show on front of lower z-indices).
    */
  def withZIndex[T](zIndex: Int)(body: (UiContext) ?=> T)(using uiContext: UiContext): T =
    val oldZ = uiContext.currentZ
    uiContext.currentZ = zIndex
    val res = body(using uiContext)
    uiContext.currentZ = oldZ
    res
