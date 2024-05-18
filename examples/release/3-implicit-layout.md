# 3. Explicit Layout

Welcome to the InterIm tutorial!

## Running the examples

You can run the code in this file (and other tutorials) with:

```bash
scala-cli 3-layout.md example-minart-backend.scala
```

Other examples can be run in a similar fashion

## Implicit layouts

In the previous example, you might have noticed something odd: helpers like `columns` use a context function instead of a
regular function.

Indeed, that's because those functions introduce an implicit `LayoutAllocator`.
When there's no area defined, components will use the allocator from the current context to pick an area implicitly.

Right now, however, while primitives (such as `rectangle` and `text`) can use an allocator, that must be passed explicitly
(also in the `area` parameter).

## Using implicit layouts in the counter application

Here's the previous example but with implicit layouts:

```scala
import eu.joaocosta.interim.*

val uiContext = new UiContext()
var counter = 0

def application(inputState: InputState) =
  import eu.joaocosta.interim.InterIm.*
  ui(inputState, uiContext):
    columns(area = Rect(x = 10, y = 10, w = 110, h = 30), numColumns = 3, padding = 10):
      button(id = "minus", label = "-"):
        counter = counter - 1
      text(
        area = summon, // we can easily get the allocator with `summon`
        color = Color(0, 0, 0),
        message = counter.toString,
        font = Font.default,
        horizontalAlignment = centerHorizontally,
        verticalAlignment = centerVertically
      )
      button(id = "plus", label = "+"):
        counter = counter + 1
```

Now let's run it:

```scala
MinartBackend.run(application)
```
