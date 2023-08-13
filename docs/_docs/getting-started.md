---
title: Getting Started
---

# Getting Started

To include InterIm, simply add the `interim` library to your project:

```scala
// JVM Only
libraryDependencies += "eu.joaocosta" %% "interim" % "{{ projectVersion }}"
// For JVM/JS/Native cross-compilation
libraryDependencies += "eu.joaocosta" %%% "interim" % "{{ projectVersion }}"
```

## Tutorial

The easiest way to start using the library is to follow the tutorials in the [`examples`](https://github.com/JD557/minart/tree/master/examples) directory.

The examples in [`examples/release`](https://github.com/JD557/minart/tree/master/examples/release) target the latest released version,
while the examples in [`examples/snapshot`](https://github.com/JD557/minart/tree/master/examples/snapshot) target the code in the repository.

All the examples are `.md` files that can be executed via [scala-cli](https://scala-cli.virtuslab.org/).

## Example backend

Since InterIm doesn't come with any graphical backend, it's quite useless to create apps with just
InterIm.

However, on the examples folder you will find a simple backend powered by [Minart](https://github.com/jd557/minart)
in `example-minart-backend.scala`.
While this backend is quite limited, it is powerful enough for most small projects.
