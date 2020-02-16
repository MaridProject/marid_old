package org.marid.ide.project.repositories

import javafx.collections.FXCollections
import javafx.scene.control.TableView
import org.marid.fx.extensions.column
import org.marid.ide.project.ProjectsTable
import org.marid.ide.project.model.FxRepository
import org.springframework.stereotype.Component

@Component
class ProjectRepositoriesTable(
  projectsTable: ProjectsTable
) : TableView<FxRepository>() {

  init {
    projectsTable.selectionModel.selectedItemProperty().addListener { _, _, n ->
      items = if (n == null) FXCollections.emptyObservableList() else n.repositories.items
    }

    columnResizePolicy = CONSTRAINED_RESIZE_POLICY

    column(200, "Name") { it.name }
    column(200, "URL") { it.url }
  }
}