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
    private[interim] var hotItem: Option[ItemId],
    private[interim] var activeItem: Option[ItemId],
    private[interim] var keyboardFocusItem: Option[ItemId],
    private[interim] val ops: mutable.TreeMap[Int, mutable.Queue[RenderOp]]
):

  private def registerItem(id: ItemId, area: Rect)(using inputState: InputState): UiContext.ItemStatus =
    if (area.isMouseOver)
      hotItem = Some(id)
      if ((activeItem == None || activeItem == Some(id)) && inputState.mouseDown)
        activeItem = Some(id)
        keyboardFocusItem = Some(id)
    UiContext.ItemStatus(hotItem == Some(id), activeItem == Some(id), keyboardFocusItem == Some(id))

  private[interim] def getOrderedOps(): List[RenderOp] =
    ops.values.toList.flatten

  def this() = this(0, None, None, None, new mutable.TreeMap())

  override def clone(): UiContext =
    new UiContext(currentZ, hotItem, activeItem, keyboardFocusItem, ops.clone().mapValuesInPlace((_, v) => v.clone()))

  def fork(): UiContext = new UiContext(currentZ, hotItem, activeItem, keyboardFocusItem, new mutable.TreeMap())

  def ++=(that: UiContext): this.type =
    this.hotItem = that.hotItem
    this.activeItem = that.activeItem
    this.keyboardFocusItem = that.keyboardFocusItem
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
    *  @param active if the mouse clicked the item (and is still pressed down)
    *  @param keyboardFocus if the keyboard events should be consumed by this item
    */
  final case class ItemStatus(hot: Boolean, active: Boolean, keyboardFocus: Boolean)

  /** Registers an item on the UI state, taking a certain area.
    *
    * Components register themselves on every frame to update and check their status.
    *
    * Note that this is only required when creating new components.
    *
    * @return the item status of the registered component.
    */
  def registerItem(id: ItemId, area: Rect)(using uiContext: UiContext, inputState: InputState): UiContext.ItemStatus =
    uiContext.registerItem(id, area)

  /** Applies the operations in a code block at a specified z-index
    *  (higher z-indices show on front of lower z-indices).
    */
  def withZIndex[T](zIndex: Int)(body: (UiContext) ?=> T)(using uiContext: UiContext): T =
    val oldZ = uiContext.currentZ
    uiContext.currentZ = zIndex
    val res = body(using uiContext)
    uiContext.currentZ = oldZ
    res
