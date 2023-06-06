package eu.joaocosta.interim.api

import eu.joaocosta.interim.TextLayout.{HorizontalAlignment, VerticalAlignment}

object Constants extends Constants

trait Constants:
  final val alignLeft          = HorizontalAlignment.Left
  final val centerHorizontally = HorizontalAlignment.Center
  final val alignRight         = HorizontalAlignment.Right

  final val alignTop         = VerticalAlignment.Top
  final val centerVertically = VerticalAlignment.Center
  final val alignBottom      = VerticalAlignment.Bottom
