package org.marid.ide.project

import javafx.scene.control.Tab
import org.marid.fx.i18n.localized
import org.marid.ide.main.IdeTabs
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ProjectsTab(contents: ProjectsTabContents) : Tab(null, contents) {
  init {
    textProperty().bind("Projects".localized)
    isClosable = false
  }

  @Autowired
  private fun addToTabs(tabs: IdeTabs) {
    tabs.tabs += this
  }
}