package org.marid.ide.project

import javafx.scene.control.SplitPane
import org.springframework.stereotype.Component

@Component
class ProjectsPane(projectsTable: ProjectsTable, projectPane: ProjectPane) : SplitPane(projectsTable, projectPane) {
  init {
    setDividerPositions(0.33)
  }
}