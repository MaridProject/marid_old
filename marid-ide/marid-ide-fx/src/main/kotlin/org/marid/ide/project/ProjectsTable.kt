package org.marid.ide.project

import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.ContextMenuEvent
import javafx.scene.layout.FlowPane
import javafx.scene.layout.Region
import javafx.util.Callback
import org.marid.fx.action.Fx
import org.marid.fx.action.configure
import org.marid.fx.action.menuItem
import org.marid.fx.action.toolButton
import org.marid.fx.extensions.bound
import org.marid.fx.extensions.column
import org.marid.fx.extensions.installEdit
import org.marid.fx.extensions.readOnlyProp
import org.springframework.stereotype.Component

@Component
class ProjectsTable(projects: Projects, private val manager: ProjectTabsManager) : TableView<Project>(projects.items) {

  init {
    columnResizePolicy = CONSTRAINED_RESIZE_POLICY
    placeholder = Label().configure(Fx("No projects yet"))
    rowFactory = Callback {
      TableRow<Project>().apply {
        contextMenu = ContextMenu()
        addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED) {
          contextMenu.items.clear()
          item?.apply { contextMenu.items += menuItems }
        }
      }
    }
    column(100, "Id") { it.id.readOnlyProp }.also {
      it.style = "-fx-alignment: CENTER; -fx-font-family: monospaced"
    }
    column(300, "Name") { listOf(it.winery.name, it.dirty).bound { it.winery.getName() to it.icon.get() } }.also {
      it.style = "-fx-alignment: CENTER-LEFT;"
      it.cellFactory = Callback {
        object : TableCell<Project, Pair<String, String>>() {
          override fun updateItem(item: Pair<String, String>?, empty: Boolean) {
            super.updateItem(item, empty)
            if (empty || item == null) {
              graphic = null
              text = null
            } else {
              graphic = ImageView(Image(item.second, 20.0, 20.0, true, true))
              text = item.first
            }
          }
        }
      }
    }
    column(150, "Group") { it.winery.group }
    column(80, "Version") { it.winery.version }
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
    installEdit { items ->
      items.forEach { manager.addProject(it) }
    }
  }

  private val Project.menuItems
    get() = listOf(
      listOf(
        Fx(icon = "icons/delete.png", text = "Delete", h = { delete() }).menuItem
      ),
      actions.map { it.menuItem }
    ).reduce { l1, l2 -> l1 + listOf(SeparatorMenuItem()) + l2 }

  private val Project.actions
    get() = listOf(
      Fx(icon = "icons/open.png", text = "Open", h = { manager.addProject(this) }),
      Fx(icon = "icons/save.png", text = "Save", h = { save() })
    )
}