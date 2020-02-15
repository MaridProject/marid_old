package org.marid.ide.common

import javafx.geometry.Side
import org.marid.fx.extensions.pref
import org.springframework.stereotype.Component

@Component
class IdePreferences {

  val primaryTabsSide = pref("primaryTabsSide", Side.TOP)
  val secondaryTabsSide = pref("secondaryTabsSide", Side.LEFT)

  val showTypes = pref("showTypes", true)
}