# InterIm

InterIm is an [Immediate mode GUI](https://en.wikipedia.org/wiki/Immediate_mode_GUI) library in pure Scala (JVM/JS/Native).

It provides methods to build an interface and return a sequence of simple render operations (render rectangles and render text).

The library does not perform any rendering. The resulting output must be interpreted by a rendering backend.
While this might sound like a limitation, it actually allows for an easy integration with other libraries.

To know more about the library and how to get started check the [examples](https://github.com/JD557/interim/tree/master/examples).

**NOTE:** This library is still in heavy development. Expect big breaking changes in future versions.

## Features

![Example of a color picker](examples/snapshot/assets/colorpicker.png)

### Primitives and Components

- Rectangles
- Text
- Buttons
- Checkboxes
- Sliders
- Text Input
- Movable components (including windows)

### Layouts

- Grid based
- Row based (equally sized or dynamically sized)
- Column based (equally sized or dynamically sized)

### Skins

- Configurable skins for all components
- Light and dark mode

## Acknowledgments

This project was heavily inspired by [Jari Komppa's Immediate Mode GUI tutorial](https://solhsa.com/imgui/) and [microui](https://github.com/rxi/microui).
