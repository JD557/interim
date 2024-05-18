# 5. Mutable References

Welcome to the InterIm tutorial!

## Running the examples

You can run the code in this file (and other tutorials) with:

```bash
scala-cli 5-refs.md example-minart-backend.scala
```

Other examples can be run in a similar fashion

## Mutable references

In some situations, even a mutable style can be quite verbose.

In the previous window above, we only care about the window area, so we are dropping one of the values with `._2`, but if we
wanted to keep the value we would need to create a temporary variable and then mutate our state with the result.

Usually, immediate mode UIs solve this by using out parameters, and this is exactly what InterIm provides with the `Ref`
abstraction.

A `Ref` is simply a wrapper for a mutable value (`final case class Ref[T](var value: T)`). That way, components can
handle the mutation themselves.

The example above could also be written as:

```scala
import eu.joaocosta.interim.*

val uiContext = new UiContext()

val windowArea = Ref(PanelState.open(Rect(x = 10, y = 10, w = 110, h = 50))) // Now a val instead of a var
var counter    = 0

def application(inputState: InputState) =
  import eu.joaocosta.interim.InterIm.*
  ui(inputState, uiContext):
    // window takes area as a ref, so will mutate the window area variable
    window(id = "window", title = "My Counter", movable = true)(area = windowArea): area =>
      columns(area = area.shrink(5), numColumns = 3, padding = 10):
        button(id = "minus", label = "-"):
          counter = counter - 1
        text(
          area = summon,
          color = Color(0, 0, 0),
          message = counter.toString,
          font = Font.default,
          horizontalAlignment = centerHorizontally,
          verticalAlignment = centerVertically
        )
        button(id = "plus", label = "+"):
          counter = counter + 1
```

Be aware that, while the code is more concise, coding with out parameters can lead to confusing code where it's hard
to find out where a value is being mutated. It's up to you to decide when to use `Ref`s and when to use plain values.

## Scoped mutable references

Having global mutable variables is usually not a good choice, and they are even worse if functions can change the values
of their arguments at will.

To avoid this, there are a few helpful methods (`Ref.withRef`/`Ref.withRefs` and the corresponding `asRef`/`asRefs`
extension methods) that let us write applications that can only mutate the state inside the UI code.

In this example we will demonstrate how `asRefs` works. This extension method decomposes a case class into a tuple
of `Ref`s that can be used inside the block. At the end of the block, a new object is created with the new values.

```scala reset
import eu.joaocosta.interim.*

val uiContext = new UiContext()

case class AppState(counter: Int = 0, windowArea: PanelState[Rect] = PanelState.open(Rect(x = 10, y = 10, w = 110, h = 50)))
val initialState = AppState()

def applicationRef(inputState: InputState, appState: AppState) =
  import eu.joaocosta.interim.InterIm.*
  ui(inputState, uiContext):
    appState.asRefs: (counter, windowArea) =>
      window(id = "window", title = "My Counter", movable = true)(area = windowArea): area =>
        columns(area = area.shrink(5), numColumns = 3, padding = 10):
          button(id = "minus", label = "-"):
            counter := counter.get - 1 // Counter is a Ref, so we need to use :=
          text(
            area = summon,
            color = Color(0, 0, 0),
            message = counter.get.toString,  // Counter is a Ref, so we need to use .get
            font = Font.default,
            horizontalAlignment = centerHorizontally,
            verticalAlignment = centerVertically
          )
          button(id = "plus", label = "+"):
            counter := counter.get + 1  // Counter is a Ref, so we need to use :=
```

Then we can run our app:

```scala
MinartBackend.run[AppState](initialState)(applicationRef)
```
