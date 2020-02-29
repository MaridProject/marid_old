package org.marid.ide.project

import javafx.scene.control.SeparatorMenuItem
import javafx.scene.control.TableRow
import javafx.scene.control.TableView
import javafx.util.Callback
import org.marid.collections.MaridCollections
import org.marid.fx.action.Fx
import org.marid.fx.action.button
import org.marid.fx.action.menuItem
import org.marid.fx.dialog.FxDialog
import org.marid.fx.extensions.*
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

    placeholder = Fx(text = "Add cellar", icon = "icons/add.png", h = { createCellar(0) }).button
      .also { it.disableProperty().bind(projectsTable.project.isNull) }

    rowFactory = Callback {
      TableRow<FxCellar>().apply {
        installContextMenu { index, item ->
          listOf(
            Fx(
              text = "Add",
              icon = "icons/add.png",
              h = { createCellar(index) }
            ).menuItem,
            Fx(
              text = "Remove",
              icon = "icons/delete.png",
              h = { items.removeAt(index) },
              disabled = (item == null).readOnlyProp
            ).menuItem,
            SeparatorMenuItem(),
            Fx(
              text = "Sort",
              icon = "icons/sort.png",
              h = { MaridCollections.sort(items, compareBy { it.getName() }) },
              disabled = items.bindEmpty.or(projectsTable.project.isNull)
            ).menuItem
          )
        }
      }
    }

    itemsProperty().bind(projectsTable.cellars)
  }

  private fun createCellar(index: Int) {
    FxDialog(CellarDialogData(projectsTable.selectionModel.selectedItem.winery, null))
      .also { it.dialogPane.setPrefSize(400.0, 300.0) }
      .also {
        it.showAndWait().ifPresent { data -> items.add(index, FxCellar().apply { name.set(data.name.get()) }) }
      }
  }
}