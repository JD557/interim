//> using scala "3.3.0"
//> using lib "eu.joaocosta::minart::0.5.2"
//> using lib "eu.joaocosta::interim::0.1.4-SNAPSHOT"

/** This file contains a simple graphical backend written in Minart.
  *
  * This code supports the bare minimum for the examples.
  */
import scala.concurrent.Future

import eu.joaocosta.minart.backend.defaults.*
import eu.joaocosta.minart.graphics.{Color => MinartColor, *}
import eu.joaocosta.minart.graphics.image.*
import eu.joaocosta.minart.runtime.*
import eu.joaocosta.minart.input.*
import eu.joaocosta.interim.*

object MinartBackend:

  // Gloop font by Polyducks: https://twitter.com/PolyDucks
  private val font =
    SpriteSheet(Image.loadBmpImage(Resource("assets/gloop.bmp")).get, 8, 8)
  private def coloredChar(char: Char, color: MinartColor): SurfaceView = font.getSprite(char.toInt).map {
    case MinartColor(255, 255, 255) => color
    case c                          => MinartColor(255, 0, 255)
  }

  private def processKeyboard(keyboardInput: KeyboardInput): String =
    import KeyboardInput.Key._
    keyboardInput.events
      .collect { case KeyboardInput.Event.Pressed(key) =>
        key
      }
      .flatMap {
        case Digit0 | NumPad0                                                            => "0"
        case Digit1 | NumPad1                                                            => "1"
        case Digit2 | NumPad2                                                            => "2"
        case Digit3 | NumPad3                                                            => "3"
        case Digit4 | NumPad4                                                            => "4"
        case Digit5 | NumPad5                                                            => "5"
        case Digit6 | NumPad6                                                            => "6"
        case Digit7 | NumPad7                                                            => "7"
        case Digit8 | NumPad8                                                            => "8"
        case Digit9 | NumPad9                                                            => "9"
        case Space                                                                       => " "
        case Backspace                                                                   => "\u0008"
        case Tab | Enter | Escape | Shift | Ctrl | Alt | Meta | Up | Down | Left | Right => ""
        case x =>
          if (keyboardInput.keysDown(Shift)) x.toString.toUpperCase()
          else x.toString.toLowerCase()
      }
      .mkString

  // Example of a loop with global mutable state
  def run(body: InputState => (List[RenderOp], _)): Future[Unit] =
    AppLoop
      .statelessRenderLoop { (canvas: Canvas) =>
        val inputState = InputState(
          canvas.getPointerInput().position.map(_.x).getOrElse(0),
          canvas.getPointerInput().position.map(_.y).getOrElse(0),
          canvas.getPointerInput().isPressed,
          processKeyboard(canvas.getKeyboardInput())
        )
        canvas.clear()
        val ops = body(inputState)._1
        ops.foreach {
          case RenderOp.DrawRect(Rect(x, y, w, h), color) =>
            canvas.fillRegion(x, y, w, h, MinartColor(color.r, color.g, color.b))
          case op: RenderOp.DrawText =>
            op.asDrawChars().foreach { case RenderOp.DrawChar(Rect(x, y, _, _), color, char) =>
              canvas
                .blit(coloredChar(char, MinartColor(color.r, color.g, color.b)), Some(MinartColor(255, 0, 255)))(x, y)
            }
          case RenderOp.Custom(Rect(x, y, w, h), color, _) =>
            canvas.fillRegion(x, y, w, h, MinartColor(color.r, color.g, color.b))
        }
        canvas.redraw()
      }
      .configure(
        Canvas.Settings(width = 640, height = 480, title = "Immediate GUI", clearColor = MinartColor(80, 110, 120)),
        LoopFrequency.hz60
      )
      .run()

  // Example of a loop with immutable state
  def run[S](initialState: S)(body: (InputState, S) => (List[RenderOp], S)): Future[S] =
    AppLoop
      .statefulRenderLoop { (state: S) => (canvas: Canvas) =>
        val inputState = InputState(
          canvas.getPointerInput().position.map(_.x).getOrElse(0),
          canvas.getPointerInput().position.map(_.y).getOrElse(0),
          canvas.getPointerInput().isPressed,
          processKeyboard(canvas.getKeyboardInput())
        )
        canvas.clear()
        val (ops, newState) = body(inputState, state)
        ops.foreach {
          case RenderOp.DrawRect(Rect(x, y, w, h), color) =>
            canvas.fillRegion(x, y, w, h, MinartColor(color.r, color.g, color.b))
          case op: RenderOp.DrawText =>
            op.asDrawChars().foreach { case RenderOp.DrawChar(Rect(x, y, _, _), color, char) =>
              canvas
                .blit(coloredChar(char, MinartColor(color.r, color.g, color.b)), Some(MinartColor(255, 0, 255)))(x, y)
            }
          case RenderOp.Custom(Rect(x, y, w, h), color, _) =>
            canvas.fillRegion(x, y, w, h, MinartColor(color.r, color.g, color.b))
        }
        canvas.redraw()
        newState
      }
      .configure(
        Canvas.Settings(width = 640, height = 480, title = "Immediate GUI", clearColor = MinartColor(80, 110, 120)),
        LoopFrequency.hz60,
        initialState
      )
      .run()
