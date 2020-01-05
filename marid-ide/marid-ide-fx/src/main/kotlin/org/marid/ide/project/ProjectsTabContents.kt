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
import org.marid.fx.i18n.localized
import org.springframework.stereotype.Component

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
            contextMenu.items += MenuItem("xx")
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
        cellValueFactory = Callback { it.value.name }
        style = "-fx-alignment: CENTER-LEFT;"
      },
      TableColumn<Project, FlowPane>().apply {
        textProperty().bind("Actions".localized)
        minWidth = 300.0
        prefWidth = 300.0
        maxWidth = 400.0
        cellValueFactory = Callback {
          val buttons = it.value.actions
            .map { a -> Button().configure(a) }
            .apply { isFocusTraversable = false }
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

  private val Project.actions
    get() = arrayOf(
      Fx(icon = "icons/delete.png", description = "Delete project", handler = { delete() }),
      Fx(icon = "icons/open.png", description = "Open project", handler = { projectTabsManager.addProject(this) }),
      Fx(icon = "icons/edit.png", description = "Edit project"),
      Fx(icon = "icons/build.png", description = "Build project", handler = { }),
      Fx(icon = "icons/run.png", description = "Run project", handler = { }),
      Fx(icon = "icons/monitor.png", description = "Monitor project", handler = { })
    )
}