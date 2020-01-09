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
import org.marid.fx.action.menuItem
import org.marid.fx.action.toolButton
import org.marid.fx.i18n.localized
import org.springframework.stereotype.Component

@Component
class ProjectsTab(contents: ProjectsTabContents) : Tab(null, contents) {
  init {
    textProperty().bind("Projects".localized)
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
      center = this
    }

  init {
    projectList.columns += listOf(
      TableColumn<Project, String>().apply {
        textProperty().bind("Name".localized)
        minWidth = 200.0
        prefWidth = 300.0
        maxWidth = 500.0
        cellValueFactory = Callback { it.value.winery.name }
        style = "-fx-alignment: CENTER-LEFT;"
      },
      TableColumn<Project, FlowPane>().apply {
        textProperty().bind("Actions".localized)
        minWidth = 300.0
        prefWidth = 300.0
        maxWidth = 400.0
        cellValueFactory = Callback {
          val buttons = it.value.actions
            .map { a -> a.toolButton.apply { isFocusTraversable = false } }
            .toTypedArray()
          SimpleObjectProperty(FlowPane(3.0, 0.0, *buttons).apply {
            prefWrapLength = Double.MAX_VALUE
            alignment = Pos.BASELINE_CENTER
            prefHeight = Region.USE_PREF_SIZE
          })
        }
      }
    )
  }

  private val Project.menuItems
    get() = listOf(
      Fx(icon = "icons/delete.png", text = "Delete", handler = { delete() }).menuItem
    ) + listOf(SeparatorMenuItem()) + actions.map { it.menuItem }

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