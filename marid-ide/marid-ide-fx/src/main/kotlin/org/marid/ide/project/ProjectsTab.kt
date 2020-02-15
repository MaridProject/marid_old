package org.marid.ide.project

import javafx.scene.control.Tab
import org.marid.fx.action.Fx
import org.marid.fx.action.configure
import org.springframework.stereotype.Component

@Component
class ProjectsTab(table: ProjectsTable) : Tab(null, table) {
  init {
    configure(Fx("Projects", "icons/project.png"))
    isClosable = false
  }
}