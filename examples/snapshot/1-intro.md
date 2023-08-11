# 1. Introduction

Welcome to the InterIm tutorial!

## Running the examples

You can run the code in this file (and other tutorials) with:

```bash
scala-cli 1-intro.md example-minart-backend.scala
```

Other examples can be run in a similar fashion

## Introductory notes

InterIm doesn't come with a graphics backend, so all examples will use an example backend powered by
[Minart](https://github.com/jd557/minart). However, you can easily use your own backend.

Also, all examples will use the ["8x8 Gloop"](https://www.gridsagegames.com/rexpaint/resources.html#Fonts) font
by [Polyducks](https://twitter.com/PolyDucks).

Finally, due to the imperative nature of Immediate Mode UIs, the examples will be extremely imperative,
with lots of mutation, as that's the idiomatic way to write such UIs.
However, InterIm also allows you to write code with almost no mutability (at the expense of verbosity).

## A simple counter application

Let's start with a simple counter application with:
- A counter showing a number
- A button that increments the counter
- A button that decrements the counter

First, let's start by setting our state:

```scala
var counter = 0
```

We also need to create a `UiState`. This is the object InterIm uses to keep it's mutable internal state
between each call.

```scala
import eu.joaocosta.interim.*

val uiState = new UiState()
```

Now, let's write our interface. We are going to need the following components:
- `text`, to draw the counter value
- `button`, to increase and decrease the value

```scala
def application(inputState: InputState) =
  import eu.joaocosta.interim.InterIm._
  ui(inputState, uiState):
    if (button(id = "minus", area = Rect(x = 10, y = 10, w = 30, h = 30), label = "-"))
      counter = counter - 1
    text(
      area = Rect(x = 40, y = 10, w = 30, h = 30),
      color = Color(0, 0, 0),
      text = counter.toString,
      font = Font.default,
      horizontalAlignment = centerHorizontally,
      verticalAlignment = centerVertically
    )
    if (button(id = "plus", area = Rect(x = 70, y = 10, w = 30, h = 30), label = "+"))
      counter = counter + 1
```

Let's go line by line:

First, our application will need to somehow receive input (e.g. mouse clicks), so we need to provide the `InputState`
from our backend.

Then, we import `import eu.joaocosta.interim.InterIm._`. This enables the InterIm DSL, which give us access to our
component functions.

Next, we start our UI with `ui(inputState, uiState)`. All DSL operation must happen inside this block which,
in the end, returns the sequence of render operations that must be executed by the backend.

Now, to the button logic:
1. Interactive components like buttons require a unique ID, which is the first parameter;
2. Then we need to specify an area where the button will be drawn;
3. We also add a label, which is the text that will be shown on the button;
4. Finally, `button` returns `true` when the button is pressed, so we use that to decrement our counter.

For the text block we don't need an id, as it's just a rendering primitive with no interaction, so we just need to
give it the string we want to show and the style details.

Finally, we add another button that increments the counter. Note that this one uses a different id!

## Integrating with the backend

Now that our application is defined, we can call it from our backend:

In pseudo code, this looks like the following:

```
val uiState = new UiState

def application(inputState: InputState) = ??? // Our application code

while(true) {
  val input: InputState = backend.grabInput() // Grab input from the backend
  val (renderOps, _) = application(input) // Generate render ops
  backend.render(renderOps) // Send render operations to the backend
}

```

In this examples, we'll simply call this basic `MinartBackend` with:

```scala
MinartBackend.run(application)
```

## A note on state and mutability

You might have noticed that our application returns two parameters, and we are ignoring the second one.
Indeed, the `ui` function (and other InterIm operations) return the last value of the body.
This makes it possible to write applications without using mutable variables.

For example we could rewrite our application as:

```scala
def immutableApp(inputState: InputState, counter: Int): (List[RenderOp], Int) =
  import eu.joaocosta.interim.InterIm._
  ui(inputState, uiState):
    val (decrementCounter, _, incrementCounter) = (
      button(id = "minus", area = Rect(x = 10, y = 10, w = 30, h = 30), label = "-"),
      text(
        area = Rect(x = 40, y = 10, w = 30, h = 30),
        color = Color(0, 0, 0),
        text = counter.toString,
        font = Font.default,
        horizontalAlignment = centerHorizontally,
        verticalAlignment = centerVertically
      ),
      button(id = "plus", area = Rect(x = 70, y = 10, w = 30, h = 30), label = "+")
    )
    if (decrementCounter && !incrementCounter) counter - 1
    else if (!decrementCounter && incrementCounter) counter + 1
    else counter
```

Unfortunately, as it might be visible from the example, when multiple components update the same state, some
boilerplate is required to unify the state changes.

One possible solution to this is to use local mutability:

```scala
def localMutableApp(inputState: InputState, counter: Int): (List[RenderOp], Int) =
  import eu.joaocosta.interim.InterIm._
  var _counter = counter
  ui(inputState, uiState):
    if (button(id = "minus", area = Rect(x = 10, y = 10, w = 30, h = 30), label = "-"))
      _counter = counter - 1
    text(
      area = Rect(x = 40, y = 10, w = 30, h = 30),
      color = Color(0, 0, 0),
      text = counter.toString,
      font = Font.default,
      horizontalAlignment = centerHorizontally,
      verticalAlignment = centerVertically
    )
    if (button(id = "plus", area = Rect(x = 70, y = 10, w = 30, h = 30), label = "+"))
      _counter = counter + 1
    _counter
```

InterIm also provides some tools to make local mutability easier and safer. Those are introduced in later examples.
