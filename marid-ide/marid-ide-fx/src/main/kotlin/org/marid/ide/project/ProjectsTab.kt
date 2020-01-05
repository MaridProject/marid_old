package org.marid.ide.project

import javafx.scene.control.Tab
import org.marid.fx.i18n.localized
import org.springframework.stereotype.Component

@Component
class ProjectsTab(contents: ProjectsTabContents) : Tab(null, contents) {
  init {
    textProperty().bind("Projects".localized)
    isClosable = false
  }
}