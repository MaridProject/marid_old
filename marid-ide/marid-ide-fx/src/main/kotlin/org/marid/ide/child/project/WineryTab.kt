package org.marid.ide.child.project

import javafx.scene.control.Tab
import javafx.scene.layout.BorderPane
import org.marid.fx.i18n.localized
import org.springframework.stereotype.Component

@Component
class WineryTab(content: WineryTabContent) : Tab(null, content) {
  init {
    textProperty().bind("Winery".localized)
    isClosable = false
  }
}

@Component
class WineryTabContent : BorderPane() {
}