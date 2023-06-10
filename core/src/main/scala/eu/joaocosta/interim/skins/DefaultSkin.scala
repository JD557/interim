package eu.joaocosta.interim.skins

/** Default Skin companion object. Includes both a light and dark mode.
  */
trait DefaultSkin[Skin]:
  def default(): Skin =
    if (ColorScheme.lightModeEnabled()) lightDefault
    else darkDefault

  def lightDefault: Skin
  def darkDefault: Skin
