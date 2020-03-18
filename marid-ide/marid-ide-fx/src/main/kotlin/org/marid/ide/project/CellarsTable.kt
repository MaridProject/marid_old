package org.marid.ide.project

import javafx.scene.control.TableRow
import javafx.scene.control.TableView
import javafx.util.Callback
import org.marid.fx.action.Fx
import org.marid.fx.action.button
import org.marid.fx.dialog.FxDialog
import org.marid.fx.extensions.bindSize
import org.marid.fx.extensions.column
import org.marid.fx.table.TableRowContextMenu
import org.marid.ide.project.dialogs.CellarDialogData
import org.marid.ide.project.model.FxCellar
import org.springframework.stereotype.Component

@Component
class CellarsTable(private val projectsTable: ProjectsTable) : TableView<FxCellar>() {

  init {
    columnResizePolicy = CONSTRAINED_RESIZE_POLICY

    column(300, "Name") { it.name }
    column(100, "Racks") { it.racks.bindSize }
    column(100, "Constants") { it.constants.bindSize }

    placeholder = Fx(
      text = "Add cellar",
      icon = "icons/add.png",
      h = { createCellar()?.also { items.add(it) } },
      disabled = projectsTable.project.isNull
    ).button

    rowFactory = Callback {
      TableRow<FxCellar>().apply {
        contextMenu = TableRowContextMenu(this).also { menu ->
          menu.installSort { it.getName() }
          menu.installAdd(true) { createCellar() }
          menu.installDelete()
          menu.installReorder()
        }
      }
    }

    itemsProperty().bind(projectsTable.cellars)
  }

  private fun createCellar() = FxDialog(CellarDialogData(projectsTable.selectionModel.selectedItem.winery, null))
    .also { it.dialogPane.setPrefSize(400.0, 300.0) }
    .showAndWait()
    .map { data -> FxCellar().also { it.setName(data.name.get()) } }
    .orElse(null)
}