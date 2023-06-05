package eu.joaocosta.guila

final case class Rect(x: Int, y: Int, w: Int, h: Int):
  def isMouseOver(implicit inputState: InputState): Boolean =
    !(inputState.mouseX < x || inputState.mouseY < y || inputState.mouseX >= x + w || inputState.mouseY >= y + h)
