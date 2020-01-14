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
import org.marid.fx.extensions.readOnlyProp
import org.marid.fx.i18n.localized
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

  private val projectList = TableView(projects.items).apply {
    rowFactory = Callback {
      TableRow<Project>().apply {
        contextMenu = ContextMenu()
        addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED) {
          contextMenu.items.clear()
          item?.apply { contextMenu.items += menuItems }
        }
      }
    }
    columns += listOf(
      TableColumn<Project, String>().apply {
        textProperty().bind("Id".localized)
        minWidth = 128.0; prefWidth = 160.0; maxWidth = 200.0
        cellValueFactory = Callback { it.value.id.readOnlyProp }
        style = "-fx-alignment: CENTER; -fx-font-family: monospaced"
      },
      TableColumn<Project, String>().apply {
        textProperty().bind("Name".localized)
        minWidth = 200.0; prefWidth = 300.0; maxWidth = 500.0
        cellValueFactory = Callback { it.value.winery.name }
        style = "-fx-alignment: CENTER-LEFT;"
      },
      TableColumn<Project, FlowPane>().apply {
        textProperty().bind("Actions".localized)
        minWidth = 300.0; prefWidth = 300.0; maxWidth = 400.0
        cellValueFactory = Callback {
          val buttons = it.value.actions.map(Fx::toolButton).toTypedArray()
          SimpleObjectProperty(
            FlowPane(3.0, 0.0, *buttons).apply {
              prefWrapLength = Double.MAX_VALUE
              alignment = Pos.BASELINE_CENTER
              prefHeight = Region.USE_PREF_SIZE
            }
          )
        }
      }
    )
  }

  init {
    center = projectList
  }

  private val Project.menuItems
    get() = listOf(
      listOf(
        Fx(icon = "icons/delete.png", text = "Delete", handler = { delete() }).menuItem
      ),
      actions.map { it.menuItem },
      listOf(
        Fx(icon = "icons/clear.png", text = "Clean", handler = { clean() }).menuItem
      )
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