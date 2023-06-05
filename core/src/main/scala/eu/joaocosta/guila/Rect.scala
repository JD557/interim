package eu.joaocosta.guila

final case class Rect(x: Int, y: Int, w: Int, h: Int):
  def x1 = x
  def y1 = y
  def x2 = x + w
  def y2 = y + h

  def isMouseOver(implicit inputState: InputState): Boolean =
    !(inputState.mouseX < x || inputState.mouseY < y || inputState.mouseX >= x + w || inputState.mouseY >= y + h)

  def move(dx: Int, dy: Int): Rect =
    copy(x = x + dx, y = y + dy)

  def shrink(size: Int): Rect =
    Rect(x + size, y + size, w - size * 2, h - size * 2)

  def grow(size: Int): Rect =
    Rect(x - size, y - size, w + size * 2, h + size * 2)

  def ||(that: Rect): Rect =
    val minX = math.min(this.x1, that.x1)
    val maxX = math.max(this.x2, that.x2)
    val minY = math.min(this.y1, that.y1)
    val maxY = math.max(this.y2, that.y2)
    Rect(x = minX, y = minY, w = maxX - minX, h = maxY - minY)
