package eu.joaocosta.interim.skins

import eu.joaocosta.interim.Color

final case class ColorScheme(
    background: Color,         // white / black
    text: Color,               // black / white
    icon: Color,               // black / lightGray
    iconHighlight: Color,      // pureGray / white
    primary: Color,            // lightPrimary / darkPrimary
    primaryShadow: Color,      // lightPrimaryShadow / darkPrimaryShadow
    primaryHighlight: Color,   // lightPrimaryHighlight / darkPrimaryHighlight
    secondary: Color,          // lightGray / darkGray
    secondaryHighlight: Color, // pureGray / pureGray
    borderColor: Color         // pureGray / pureGray
)

/** Internal default color scheme used by InterIm's default skins */
object ColorScheme:
  val white     = Color(246, 247, 251)
  val lightGray = Color(177, 186, 177)
  val pureGray  = Color(127, 127, 127)
  val darkGray  = Color(77, 77, 77)
  val black     = Color(23, 21, 23)

  val lightPrimary          = Color(9, 211, 222)
  val lightPrimaryShadow    = Color(15, 172, 186)
  val lightPrimaryHighlight = Color(0, 247, 255)

  val darkPrimary          = Color(97, 31, 125)
  val darkPrimaryShadow    = Color(67, 24, 92)
  val darkPrimaryHighlight = Color(130, 38, 158)

  val lightScheme = ColorScheme(
    background = white,
    text = black,
    icon = black,
    iconHighlight = pureGray,
    primary = lightPrimary,
    primaryShadow = lightPrimaryShadow,
    primaryHighlight = lightPrimaryHighlight,
    secondary = lightGray,
    secondaryHighlight = pureGray,
    borderColor = pureGray
  )
  val darkScheme = ColorScheme(
    background = black,
    text = white,
    icon = lightGray,
    iconHighlight = white,
    primary = darkPrimary,
    primaryShadow = darkPrimaryShadow,
    primaryHighlight = darkPrimaryHighlight,
    secondary = darkGray,
    secondaryHighlight = pureGray,
    borderColor = pureGray
  )

  private var darkMode = false

  /** Forces default skins to use the dark mode */
  def useDarkMode() = darkMode = true

  /** Forces default skins to use the light mode */
  def useLightMode() = darkMode = false

  /** Checks if dark mode is enabed for default skins */
  def darkModeEnabled() = darkMode

  /** Checks if light mode is enabed for default skins */
  def lightModeEnabled() = !darkMode
