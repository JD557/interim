package eu.joaocosta.interim.api

import eu.joaocosta.interim.{HorizontalAlignment, VerticalAlignment}

/** Object containing some aliases for some constants.
  */
object Constants extends Constants

trait Constants:
  final val alignLeft: HorizontalAlignment.Left.type            = HorizontalAlignment.Left
  final val centerHorizontally: HorizontalAlignment.Center.type = HorizontalAlignment.Center
  final val alignRight: HorizontalAlignment.Right.type          = HorizontalAlignment.Right

  final val alignTop: VerticalAlignment.Top.type            = VerticalAlignment.Top
  final val centerVertically: VerticalAlignment.Center.type = VerticalAlignment.Center
  final val alignBottom: VerticalAlignment.Bottom.type      = VerticalAlignment.Bottom

  final val maxSize: Int.MaxValue.type = Int.MaxValue
