package eu.joaocosta.interim.skins

/** Default Skin companion object. Includes both a light and dark mode.
  */
trait DefaultSkin:
  type Default

  def default(): Default =
    if (ColorScheme.lightModeEnabled()) lightDefault
    else darkDefault

  def lightDefault: Default
  def darkDefault: Default
