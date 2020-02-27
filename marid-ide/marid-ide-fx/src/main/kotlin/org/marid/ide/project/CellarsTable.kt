package org.marid.ide.project

import javafx.scene.control.TableView
import org.marid.fx.action.Fx
import org.marid.fx.action.button
import org.marid.fx.dialog.FxDialog
import org.marid.fx.extensions.bindSize
import org.marid.fx.extensions.column
import org.marid.ide.project.dialogs.CellarDialogData
import org.marid.ide.project.model.FxCellar
import org.springframework.stereotype.Component

@Component
class CellarsTable(private val projectsTable: ProjectsTable) : TableView<FxCellar>() {

  private val project = projectsTable.selectionModel.selectedItemProperty()

  init {
    columnResizePolicy = CONSTRAINED_RESIZE_POLICY
    maxHeight = 300.0

    column(300, "Name") { it.name }
    column(100, "Racks") { it.racks.bindSize }
    column(100, "Constants") { it.constants.bindSize }

    placeholder = Fx(text = "Add cellar", icon = "icons/add.png", handler = { createCellar(0) }).button
      .also { it.disableProperty().bind(project.isNull) }
  }

  private fun createCellar(index: Int) {
    FxDialog(CellarDialogData(projectsTable.selectionModel.selectedItem.winery, null))
      .also { it.dialogPane.setPrefSize(400.0, 300.0) }
      .also {
        it.showAndWait().ifPresent { data -> items.add(index, FxCellar().apply { name.set(data.name.get()) }) }
      }
  }
}