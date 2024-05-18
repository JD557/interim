package eu.joaocosta.interim

/** A Component is something that can be shown in the UI.
  *
  * It uses the current input state and UI context to draw itself and returns the current value.
  */
type Component[+T] = (inputState: InputState.Historical, uiContext: UiContext) ?=> T

/** A Component that returns a value.
  */
trait ComponentWithValue[T]:
  def render(area: Rect, value: Ref[T]): Component[Unit]

  def applyRef(area: Rect, value: Ref[T]): Component[T] =
    render(area, value)
    value.get

  def applyValue(area: Rect, value: T): Component[T] =
    apply(area, Ref(value))

  inline def apply(area: Rect, value: T | Ref[T]): Component[T] = inline value match
    case x: T      => applyValue(area, x)
    case x: Ref[T] => applyRef(area, x)

/** A Component that returns a value.
  *
  * The area can be computed dynamically based on a layout allocator.
  */
trait DynamicComponentWithValue[T] extends ComponentWithValue[T]:
  def allocateArea(using allocator: LayoutAllocator.AreaAllocator): Rect

  def render(value: Ref[T])(using allocator: LayoutAllocator.AreaAllocator): Component[Unit] =
    render(allocateArea, value)

  inline def apply(value: T | Ref[T])(using allocator: LayoutAllocator.AreaAllocator): Component[T] =
    apply(allocateArea, value)

/** A Component that computes its value based on a body.
  */
trait ComponentWithBody[I, F[_]]:
  def render[T](area: Rect, body: I => T): Component[F[T]]

  def apply[T](area: Rect)(body: I => T): Component[F[T]] = render(area, body)

  def apply[T](area: Rect)(body: => T)(using ev: I =:= Unit): Component[F[T]] = render(area, _ => body)

/** A Component that computes its value based on a body.
  *
  * The area can be computed dynamically based on a layout allocator.
  */
trait DynamicComponentWithBody[I, F[_]] extends ComponentWithBody[I, F]:
  def allocateArea(using allocator: LayoutAllocator.AreaAllocator): Rect

  def render[T](body: I => T)(using allocator: LayoutAllocator.AreaAllocator): Component[Unit] =
    render(allocateArea, body)

  def apply[T](body: I => T)(using allocator: LayoutAllocator.AreaAllocator): Component[F[T]] =
    render(allocateArea, body)

  def apply[T](body: => T)(using allocator: LayoutAllocator.AreaAllocator, ev: I =:= Unit): Component[F[T]] =
    render(allocateArea, _ => body)
