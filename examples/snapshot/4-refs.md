# 4. Mutable References

Welcome to the InterIm tutorial!

## Running the examples

You can run the code in this file (and other tutorials) with:

```bash
scala-cli 4-refs.md example-minart-backend.scala
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
import eu.joaocosta.interim.api.Ref

val uiState = new UiState()

val windowArea = Ref(Rect(x = 10, y = 10, w = 110, h = 50)) // Now a val instead of a var
var counter    = 0

def application(inputState: InputState) =
  import eu.joaocosta.interim.InterIm._
  ui(inputState, uiState):
    // window takes area as a ref, so will mutate the window area variable
    window(id = "window", area = windowArea, title = "My Counter", movable = true) { area =>
      columns(area = area.shrink(5), numColumns = 3, padding = 10) { column =>
        if (button(id = "minus", area = column(0), label = "-"))
          counter = counter - 1
        text(
          area = column(1),
          color = Color(0, 0, 0),
          text = counter.toString,
          fontSize = 8,
          horizontalAlignment = centerHorizontally,
          verticalAlignment = centerVertically
        )
        if (button(id = "plus", area = column(2), label = "+"))
          counter = counter + 1
      }
    }
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
import eu.joaocosta.interim.api.Ref

val uiState = new UiState()

case class AppState(counter: Int = 0, windowArea: Rect = Rect(x = 10, y = 10, w = 110, h = 50))
val initialState = AppState()

def applicationRef(inputState: InputState, appState: AppState) =
  import eu.joaocosta.interim.InterIm._
  import eu.joaocosta.interim.api.Ref.asRefs
  ui(inputState, uiState):
    appState.asRefs { (counter, windowArea) =>
      window(id = "window", area = windowArea, title = "My Counter", movable = true) { area =>
        columns(area = area.shrink(5), numColumns = 3, padding = 10) { column =>
          if (button(id = "minus", area = column(0), label = "-"))
            counter := counter.get - 1 // Counter is a Ref, so we need to use :=
          text(
            area = column(1),
            color = Color(0, 0, 0),
            text = counter.get.toString,  // Counter is a Ref, so we need to use .get
            fontSize = 8,
            horizontalAlignment = centerHorizontally,
            verticalAlignment = centerVertically
          )
          if (button(id = "plus", area = column(2), label = "+"))
            counter := counter.get + 1  // Counter is a Ref, so we need to use :=
        }
      }
    }
```

Then we can run our app:

```scala
MinartBackend.run[AppState](initialState)(applicationRef)
```