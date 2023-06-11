//> using scala "3.3.0"
//> using lib "org.creativescala::doodle::0.18.0"
//> using lib "eu.joaocosta::interim::0.1.0"

/** This file contains a example of an alternatve graphical backend written in Doodle.
  *
  * This example currently does not support any input. It's just to show how other libraries can be used.
  */

import cats.effect.unsafe.implicits.global
import doodle.core.{Color => DoodleColor, *}
import doodle.interact.syntax.all.*
import doodle.interact.animation.*
import doodle.java2d.*
import doodle.syntax.all.*
import eu.joaocosta.interim.*

object DoodleExample:
  // Backend Code
  private def buildPicture(renderOps: List[RenderOp]) =
    renderOps.foldLeft(Picture.empty) {
      case (picture, RenderOp.DrawRect(Rect(x, y, w, h), Color(r, g, b, _))) =>
        Picture
          .rectangle(w, h)
          .fillColor(DoodleColor.rgb(r, g, b))
          .at(x, -y)
          .on(picture)
      case (picture, RenderOp.DrawText(Rect(x, y, w, h), Color(r, g, b, _), text, _, _, _)) =>
        Picture
          .text(text)
          .at(x, -y)
          .on(picture)
      case (picture, RenderOp.Custom(Rect(x, y, w, h), Color(r, g, b, _), _)) =>
        Picture
          .rectangle(w, h)
          .fillColor(DoodleColor.rgb(r, g, b))
          .at(x, -y)
          .on(picture)
    }

  def run(body: InputState => (List[RenderOp], _)): Unit =
    val frame = Frame.default.withSize(640, 480)
    Transducer
      .scanLeft[List[RenderOp]](Nil) { _ =>
        val inputState = InputState(0, 0, false, "")
        body(inputState)._1
      }
      .map(buildPicture)
      .repeatForever
      .animate(frame)

  // Simple counter application
  var counter = 0
  val uiState = new UiState()

  private def application(inputState: InputState) =
    import eu.joaocosta.interim.InterIm._
    ui(inputState, uiState):
      if (button(id = "minus", area = Rect(x = 10, y = 10, w = 30, h = 30), label = "-"))
        counter = counter - 1
      text(
        area = Rect(x = 40, y = 10, w = 30, h = 30),
        color = Color(0, 0, 0),
        text = counter.toString,
        fontSize = 8,
        horizontalAlignment = centerHorizontally,
        verticalAlignment = centerVertically
      )
      if (button(id = "plus", area = Rect(x = 70, y = 10, w = 30, h = 30), label = "+"))
        counter = counter + 1

  @main def main(): Unit =
    run(application)
