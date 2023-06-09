package eu.joaocosta.interim

import scala.annotation.alpha

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

  /** Checks if the mouse is over this area.
    */
  def isMouseOver(implicit inputState: InputState): Boolean =
    !(inputState.mouseX < x || inputState.mouseY < y || inputState.mouseX >= x + w || inputState.mouseY >= y + h)

  /** Translates the area to another position.
    */
  def move(dx: Int, dy: Int): Rect =
    copy(x = x + dx, y = y + dy)

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
  @alpha("merge")
  def ++(that: Rect): Rect =
    val minX = math.min(this.x1, that.x1)
    val maxX = math.max(this.x2, that.x2)
    val minY = math.min(this.y1, that.y1)
    val maxY = math.max(this.y2, that.y2)
    Rect(x = minX, y = minY, w = maxX - minX, h = maxY - minY)
