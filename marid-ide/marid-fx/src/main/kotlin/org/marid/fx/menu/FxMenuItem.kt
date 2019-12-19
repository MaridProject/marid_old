package org.marid.fx.menu

import javafx.scene.control.MenuItem
import javafx.scene.image.ImageView
import org.marid.fx.i18n.localized

class FxMenuItem(text: String, icon: String?) : MenuItem() {
  init {
    textProperty().bind(text.localized)
    if (icon != null) {
      graphic = ImageView(icon)
    }
  }
}