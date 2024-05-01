# 3. Windows

Welcome to the InterIm tutorial!

## Running the examples

You can run the code in this file (and other tutorials) with:

```bash
scala-cli 3-windows.md example-minart-backend.scala
```

Other examples can be run in a similar fashion

## Floating Windows in InterIm

An advanced use case for InterIm is to draw components on top of already existing applications (e.g. to create a debug
menu).

For this, it's helpful to have a floating window abstraction, and that's exactly what `window` is!

A window is a special component that:
  - Passes an area to a function, which is the window drawable region
  - Returns it's value (optional) and a `PanelState[Rect]`. That `PanelState[Rect]` is the one that needs to be passed as the window area.

This might sound a little convoluted, but this is what allows windows to be dragged and closed.

## Using window in the counter application

Let's go straight to the example, as things are not as confusing as they sound.

```scala
import eu.joaocosta.interim.*

val uiContext = new UiContext()

var windowArea = PanelState.open(Rect(x = 10, y = 10, w = 110, h = 50))
var counter    = 0

def application(inputState: InputState) =
  import eu.joaocosta.interim.InterIm.*
  ui(inputState, uiContext):
    windowArea = window(id = "window", area = windowArea, title = "My Counter", movable = true, closable = false) { area =>
      columns(area = area.shrink(5), numColumns = 3, padding = 10) { column =>
        button(id = "minus", area = column, label = "-"):
          counter = counter - 1
        text(
          area = column,
          color = Color(0, 0, 0),
          message = counter.toString,
          font = Font.default,
          horizontalAlignment = centerHorizontally,
          verticalAlignment = centerVertically
        )
        button(id = "plus", area = column, label = "+"):
          counter = counter + 1
      }
    }._2 // We don't care about the value, just the rect
```

We now have a window that we can drag across the screen! Pick it up with the top left icon and try it.

Note how we just pass the area to our layout, so everything just moves with our window.
In this example we use `area.shrink(5)` to reduce our area by 5px on all sides, which gives the contents a nice padding.

Let's run it:

```scala
MinartBackend.run(application)
```
