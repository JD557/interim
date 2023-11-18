package eu.joaocosta.interim

/** State of a panel that can either be open or closed.
  *  Can also carry a value.
  */
final case class PanelState[T](isOpen: Boolean, value: T):
  def isClosed: Boolean    = !isOpen
  def open: PanelState[T]  = copy(isOpen = true)
  def close: PanelState[T] = copy(isOpen = false)

object PanelState:
  def open[T](value: T): PanelState[T]   = PanelState(true, value)
  def closed[T](value: T): PanelState[T] = PanelState(false, value)
