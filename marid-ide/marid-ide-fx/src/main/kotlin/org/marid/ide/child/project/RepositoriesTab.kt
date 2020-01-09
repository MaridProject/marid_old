package org.marid.ide.child.project

import javafx.scene.control.Tab
import javafx.scene.layout.BorderPane
import org.marid.fx.i18n.localized
import org.springframework.stereotype.Component

@Component
class RepositoriesTab(contents: RepositoriesTabContents) : Tab(null, contents) {
  init {
    isClosable = false
    textProperty().bind("Repositories".localized)
  }
}

@Component
class RepositoriesTabContents : BorderPane() {

}