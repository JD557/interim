package eu.joaocosta.interim

import scala.annotation.targetName

/** Rectangle abstraction, which represents an area with positive width and height.
  *
  * (x, y) is the top left coordinate.
  *
  * Alternatively, (x1, y1) is the top left coordinate and (x2, y2) is the bottom right one.
  */
final case class Rect(x: Int, y: Int, w: Int, h: Int):
  def x1 = x
  def y1 = y
  def x2 = x + w
  def y2 = y + h

  /** Returns true if the rectangle has no area
    */
  def isEmpty: Boolean = w <= 0 || h <= 0

  /** Checks if the mouse is over this area.
    */
  def isMouseOver(using inputState: InputState): Boolean =
    !(inputState.mouseInput.x < x || inputState.mouseInput.y < y || inputState.mouseInput.x >= x + w || inputState.mouseInput.y >= y + h)

  /** Translates the area to another position.
    */
  def move(dx: Int, dy: Int): Rect =
    copy(x = x + dx, y = y + dy)

  /** Resizes this rectangle by increasing the width and height.
    */
  def resize(dw: Int, dh: Int): Rect =
    copy(w = w + dw, h = h + dh)

  /** Shrinks this area by removing `size` pixels from each side.
    */
  def shrink(size: Int): Rect =
    Rect(x + size, y + size, w - size * 2, h - size * 2)

  /** Grows this area by removing `size` pixels from each side.
    */
  def grow(size: Int): Rect =
    Rect(x - size, y - size, w + size * 2, h + size * 2)

  /** Swaps the width and height of this area.
    */
  def transpose: Rect =
    copy(w = h, h = w)

  /** Merges this rectangle with another one.
    *
    * Gaps between the rectangles will also be considered as part of the final area.
    */
  @targetName("merge")
  def ++(that: Rect): Rect =
    val minX = math.min(this.x1, that.x1)
    val maxX = math.max(this.x2, that.x2)
    val minY = math.min(this.y1, that.y1)
    val maxY = math.max(this.y2, that.y2)
    Rect(x = minX, y = minY, w = maxX - minX, h = maxY - minY)

  /** Intersects this rectangle with another one.
    */
  @targetName("intersect")
  def &(that: Rect): Rect =
    val maxX1 = math.max(this.x1, that.x1)
    val maxY1 = math.max(this.y1, that.y1)
    val minX2 = math.min(this.x2, that.x2)
    val minY2 = math.min(this.y2, that.y2)
    Rect(x = maxX1, y = maxY1, w = minX2 - maxX1, h = minY2 - maxY1)
