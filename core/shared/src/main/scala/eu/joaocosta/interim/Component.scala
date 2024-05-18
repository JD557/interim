package eu.joaocosta.interim

type Component[+T] = (inputState: InputState.Historical, uiContext: UiContext) ?=> T

trait ComponentWithValue[T]:
  def allocateArea(using allocator: LayoutAllocator.AreaAllocator): Rect

  def render(area: Rect, value: Ref[T]): Component[Unit]

  def render(value: Ref[T])(using allocator: LayoutAllocator.AreaAllocator): Component[Unit] =
    render(allocateArea, value)

  def applyRef(area: Rect, value: Ref[T]): Component[T] =
    render(area, value)
    value.get

  def applyValue(area: Rect, value: T): Component[T] =
    apply(area, Ref(value))

  inline def apply(area: Rect, value: T | Ref[T]): Component[T] = inline value match
    case x: T      => applyValue(area, x)
    case x: Ref[T] => applyRef(area, x)

  inline def apply(value: T | Ref[T])(using allocator: LayoutAllocator.AreaAllocator): Component[T] =
    apply(allocateArea, value)

trait ComponentWithBody[I, F[_]]:
  def allocateArea(using allocator: LayoutAllocator.AreaAllocator): Rect

  def render[T](area: Rect, body: I => T): Component[F[T]]

  def render[T](body: I => T)(using allocator: LayoutAllocator.AreaAllocator): Component[Unit] =
    render(allocateArea, body)

  def apply[T](area: Rect)(body: I => T): Component[F[T]] = render(area, body)

  def apply[T](area: Rect)(body: => T)(using ev: I =:= Unit): Component[F[T]] = render(area, _ => body)

  def apply[T](body: I => T)(using allocator: LayoutAllocator.AreaAllocator): Component[F[T]] =
    render(allocateArea, body)

  def apply[T](body: => T)(using allocator: LayoutAllocator.AreaAllocator, ev: I =:= Unit): Component[F[T]] =
    render(allocateArea, _ => body)
