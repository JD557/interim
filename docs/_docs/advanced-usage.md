---
title: Advanced Usage
---

# Advanced Usage

## Custom Backends

InterIm applications will usually be function of the type
`(InputState, UiContext) => (List[RenderOp], Unit)` or `(InputState, UiContext, ApplicationState) => (List[RenderOp], ApplicationState)` (depending on how pure the application is).

For simplicity, let's assume a mutable app in this example.

```scala
def application(inputState: InputState, uiState: UiState) = ??? // Our application code
```

The first thing that the backend needs to do is to create an `UiContext`. This is a class that will keep the internal mutable state of the UI.

```scala
val uiContext = new UiContext()
```

Then, in the render loop, the backend needs to:
- Build the input state
- Call the application code
- Render the operations

```scala
while (true) // Hypothetical render loop
  val inputState: InputState =
    InputState(
      mouseX        = ???,
      mouseY        = ???,
      mouseDown     = ???,
      keyboardInput = ???
    )
  val (renderOps, _) = application(inputState, uiContext)
  renderOps.foreach {
    case op: RenderOp.DrawRect => ??? // Draw Rectangle
    case op: RenderOp.DrawText => ??? // Draw Text
    case op: RenderOp.Custom => ??? // Custom logic
  }
```

And that's it!

## Custom operations

You might have noticed the `RenderOp.Custom` in the example above.

This operation is designed so that you can extend InterIm with your own operations (e.g. draw a circle). The default InterIm components will never use this operation.

Custom operations are defined as:
```scala
final case class Custom[T](area: Rect, color: Color, data: T) extends RenderOp
```

Notice the `area` and the `color`. This allows you to simply draw a colored rectangle when you receive a custom operation that your backend is not able to interpret.
