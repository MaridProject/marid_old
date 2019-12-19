package org.marid.fx.menu

import javafx.scene.control.Menu
import org.marid.fx.i18n.localized

class FxMenu(text: String) : Menu() {
  init {
    textProperty().bind(text.localized)
  }
}