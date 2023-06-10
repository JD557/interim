package eu.joaocosta.interim.skins

import eu.joaocosta.interim.Color

/** Internal default color scheme used by InterIm's default skins */
object ColorScheme:
  val white     = Color(246, 247, 251)
  val lightGray = Color(177, 186, 177)
  val darkGray  = Color(77, 77, 77)
  val black     = Color(23, 21, 23)

  val lightPrimary          = Color(9, 211, 222)
  val lightPrimaryHighlight = Color(55, 221, 230)

  val darkPrimary          = Color(97, 31, 125)
  val darkPrimaryHighlight = Color(121, 53, 150)

  private var darkMode = false

  /** Forces default skins to use the dark mode */
  def useDarkMode() = darkMode = true

  /** Forces default skins to use the light mode */
  def useLightMode() = darkMode = false

  /** Checks if dark mode is enabed for default skins */
  def darkModeEnabled() = darkMode

  /** Checks if light mode is enabed for default skins */
  def lightModeEnabled() = !darkMode
