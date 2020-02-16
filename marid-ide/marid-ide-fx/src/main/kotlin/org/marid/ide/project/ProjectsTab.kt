package org.marid.ide.project

import javafx.scene.control.Tab
import org.marid.fx.action.Fx
import org.marid.fx.action.configure
import org.springframework.stereotype.Component

@Component
class ProjectsTab(projectsPane: ProjectsPane) : Tab(null, projectsPane) {
  init {
    configure(Fx("Projects", "icons/project.png"))
    isClosable = false
  }
}