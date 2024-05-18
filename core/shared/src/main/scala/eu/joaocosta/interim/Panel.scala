package eu.joaocosta.interim

/*
 * Panels are a mix of a component and a layout. They perform rendering operations, but also provide a draw area.
 */
trait Panel[I, F[_]]:
  def render[T](area: Ref[PanelState[Rect]], body: I => T): Component[F[T]]

  def apply[T](area: Ref[PanelState[Rect]])(body: I => T): Component[F[T]] =
    render(area, body)

  def apply[T](area: PanelState[Rect])(body: I => T): Component[F[T]] =
    render(Ref(area), body)

  def apply[T](area: Rect)(body: I => T): Component[F[T]] =
    render(Ref(PanelState.open(area)), body)
