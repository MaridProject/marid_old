package org.marid.ide.project

import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.input.ContextMenuEvent
import javafx.scene.layout.BorderPane
import javafx.scene.layout.FlowPane
import javafx.scene.layout.Region
import javafx.util.Callback
import org.marid.fx.action.Fx
import org.marid.fx.action.configure
import org.marid.fx.action.menuItem
import org.marid.fx.action.toolButton
import org.marid.fx.extensions.column
import org.marid.fx.extensions.installEdit
import org.marid.fx.extensions.readOnlyProp
import org.springframework.stereotype.Component

@Component
class ProjectsTab(contents: ProjectsTabContents) : Tab(null, contents) {
  init {
    configure(Fx("Projects", "icons/project.png"))
    isClosable = false
  }
}

@Component
class ProjectsTabContents(
  private val projects: Projects,
  private val projectTabsManager: ProjectTabsManager
) : BorderPane() {

  private val projectList = TableView(projects.items)
    .apply {
      rowFactory = Callback {
        TableRow<Project>().apply {
          contextMenu = ContextMenu()
          addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED) {
            contextMenu.items.clear()
            item?.apply { contextMenu.items += menuItems }
          }
        }
      }
    }
    .apply {
      column(160, "Id") { it.id.readOnlyProp }.also { it.style = "-fx-alignment: CENTER; -fx-font-family: monospaced" }
      column(300, "Name") { it.winery.name }.also { it.style = "-fx-alignment: CENTER-LEFT;" }
      column(300, "Actions") {
        val buttons = it.actions.map(Fx::toolButton).toTypedArray()
        SimpleObjectProperty(
          FlowPane(3.0, 0.0, *buttons).apply {
            prefWrapLength = Double.MAX_VALUE
            alignment = Pos.BASELINE_CENTER
            prefHeight = Region.USE_PREF_SIZE
          }
        )
      }
    }
    .apply {
      installEdit { items ->
        items.forEach { projectTabsManager.addProject(it) }
      }
    }

  init {
    center = projectList
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
      Fx(icon = "icons/open.png", text = "Open", handler = { projectTabsManager.addProject(this) }),
      Fx(icon = "icons/save.png", text = "Save", handler = { save() }),
      Fx(icon = "icons/edit.png", text = "Edit..."),
      Fx(icon = "icons/build.png", text = "Build", handler = { }),
      Fx(icon = "icons/run.png", text = "Run", handler = { }),
      Fx(icon = "icons/monitor.png", text = "Monitor...", handler = { })
    )
}