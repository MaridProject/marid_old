package org.marid.ide.project

import javafx.scene.control.Accordion
import javafx.scene.control.TitledPane
import org.marid.fx.i18n.localized
import org.marid.ide.project.dependencies.DependencyTable
import org.springframework.stereotype.Component

@Component
class ProjectPane(
  cellarsPane: CellarsPane,
  dependencyTable: DependencyTable,
  projectRepositoriesTable: ProjectRepositoriesTable,
  projectManagementPane: ProjectManagementPane
) : Accordion(
  TitledPane(null, cellarsPane).also {
    it.textProperty().bind("Cellars".localized)
  },
  TitledPane(null, dependencyTable).also {
    it.textProperty().bind("Dependencies".localized)
  },
  TitledPane(null, projectRepositoriesTable).also {
    it.textProperty().bind("Repositories".localized)
  },
  TitledPane(null, projectManagementPane).also {
    it.textProperty().bind("Management".localized)
  }
) {
  init {
    expandedPane = panes[0]
  }
}