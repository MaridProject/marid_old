package org.marid.ide.project.dependencies

import javafx.collections.FXCollections
import javafx.scene.control.ContextMenu
import javafx.scene.control.SeparatorMenuItem
import javafx.scene.control.TableRow
import javafx.scene.control.TableView
import javafx.scene.input.ContextMenuEvent
import javafx.util.Callback
import org.marid.fx.action.Fx
import org.marid.fx.action.menuItem
import org.marid.fx.extensions.column
import org.marid.ide.extensions.bean
import org.marid.ide.project.ProjectsTable
import org.marid.ide.project.model.FxDependency
import org.springframework.beans.factory.ObjectFactory
import org.springframework.stereotype.Component

@Component
class DependencyTable(
  projectsTable: ProjectsTable,
  private val dependencyDialogFactory: ObjectFactory<DependencyDialog>
) : TableView<FxDependency>() {

  val addDependency = Fx(
    text = "Add dependency",
    icon = "icons/add.png",
    handler = { dependencyDialogFactory.bean.showAndWait().ifPresent { items.add(it) } }
  )

  val sortDependencies = Fx(
    text = "Sort dependencies",
    icon = "icons/sort.png",
    handler = {
      items.sortWith(
        compareBy(
          { it.group.get() },
          { it.artifact.get() },
          { it.version.get() }
        )
      )
    }
  )

  init {
    projectsTable.selectionModel.selectedItemProperty().addListener { _, _, n ->
      items = if (n == null) FXCollections.emptyObservableList() else n.dependencies.items
    }

    columnResizePolicy = CONSTRAINED_RESIZE_POLICY

    column(200, "Group") { it.group }
    column(200, "Name") { it.artifact }
    column(200, "Version") { it.version }

    rowFactory = Callback {
      TableRow<FxDependency>()
        .apply {
          val menu = ContextMenu().also { contextMenu = it }
          addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED) {
            menu.items.clear()
            menu.items += listOf(
              addDependency.menuItem,
              SeparatorMenuItem(),
              sortDependencies.menuItem
            )
            item?.also { curItem ->
              menu.items += listOf(
                SeparatorMenuItem(),
                editFx(curItem).menuItem
              )
            }
          }
        }
    }
  }

  private fun editFx(dep: FxDependency) = Fx(
    text = "Edit...",
    icon = "icons/edit.png",
    handler = { dependencyDialogFactory.bean.init(dep).showAndWait().ifPresent(dep::copyFrom) }
  )
}