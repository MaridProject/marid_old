package org.marid.ide.project

import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Pos
import javafx.scene.control.ContextMenu
import javafx.scene.control.SeparatorMenuItem
import javafx.scene.control.TableRow
import javafx.scene.control.TableView
import javafx.scene.input.ContextMenuEvent
import javafx.scene.layout.FlowPane
import javafx.scene.layout.Region
import javafx.util.Callback
import org.marid.fx.action.Fx
import org.marid.fx.action.menuItem
import org.marid.fx.action.toolButton
import org.marid.fx.extensions.column
import org.marid.fx.extensions.installEdit
import org.marid.fx.extensions.readOnlyProp
import org.marid.fx.table.ReadOnlyCheckBoxTableCell
import org.springframework.stereotype.Component

@Component
class ProjectsTabTable(projects: Projects, private val manager: ProjectTabsManager) : TableView<Project>(projects.items) {

  init {
    rowFactory = Callback {
      TableRow<Project>().apply {
        contextMenu = ContextMenu()
        addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED) {
          contextMenu.items.clear()
          item?.apply { contextMenu.items += menuItems }
        }
      }
    }
    column(160, "Id") { it.id.readOnlyProp }.also { it.style = "-fx-alignment: CENTER; -fx-font-family: monospaced" }
    column(300, "Name") { it.winery.name }.also { it.style = "-fx-alignment: CENTER-LEFT;" }
    column(150, "Actions") {
      val buttons = it.actions.map(Fx::toolButton).toTypedArray()
      SimpleObjectProperty(
        FlowPane(3.0, 0.0, *buttons).apply {
          prefWrapLength = Double.MAX_VALUE
          alignment = Pos.BASELINE_CENTER
          prefHeight = Region.USE_PREF_SIZE
        }
      )
    }
    column(100, "Changed") { it.dirty }.also {
      it.cellFactory = Callback { ReadOnlyCheckBoxTableCell() }
    }
    installEdit { items ->
      items.forEach { manager.addProject(it) }
    }
  }

  private val Project.menuItems
    get() = listOf(
      listOf(
        Fx(icon = "icons/delete.png", text = "Delete", handler = { delete() }).menuItem
      ),
      actions.map { it.menuItem }
    ).reduce { l1, l2 -> l1 + listOf(SeparatorMenuItem()) + l2 }

  private val Project.actions
    get() = listOf(
      Fx(icon = "icons/open.png", text = "Open", handler = { manager.addProject(this) }),
      Fx(icon = "icons/save.png", text = "Save", handler = { save() })
    )
}