package org.marid.ide.common

import javafx.geometry.Side
import org.marid.fx.extensions.pref
import org.springframework.stereotype.Component

@Component
class IdePreferences {

  val primaryTabsSide = pref("primaryTabsSide", Side.TOP)
  val alternateTabsSide = pref("alternateTabsSide", Side.LEFT)
}