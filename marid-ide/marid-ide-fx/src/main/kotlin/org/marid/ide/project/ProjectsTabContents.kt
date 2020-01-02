package org.marid.ide.project

import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import javafx.scene.layout.FlowPane
import javafx.scene.layout.Region
import javafx.util.Callback
import org.marid.fx.action.FxAction
import org.marid.fx.action.configure
import org.marid.fx.i18n.localized
import org.marid.ide.main.IdeTabs
import org.springframework.stereotype.Component

@Component
class ProjectsTabContents(projects: Projects, tabs: IdeTabs) : BorderPane() {

  private val projectList = TableView(projects.items)
    .apply {
      rowFactory = Callback {
        TableRow<Project>()
      }
    }

  private val splitPane = SplitPane(projectList)
    .apply { center = this }

  init {
    projectList.columns += listOf(
      TableColumn<Project, String>()
        .apply {
          textProperty().bind("Name".localized)
          minWidth = 128.0
          prefWidth = 200.0
          maxWidth = 400.0
          cellValueFactory = Callback { it.value.name }
          style = "-fx-alignment: CENTER-LEFT;"
        },
      TableColumn<Project, FlowPane>()
        .apply {
          textProperty().bind("Actions".localized)
          minWidth = 300.0
          prefWidth = 300.0
          maxWidth = 400.0
          cellValueFactory = Callback { params ->
            val buttons = arrayOf(
              Button().configure(FxAction(
                icon = "icons/delete.png",
                description = "Delete project"
              ), 20),
              Button().configure(FxAction(
                icon = "icons/open.png",
                description = "Open project",
                handler = { tabs.addProject(params.value) }
              ), 20),
              Button().configure(FxAction(
                icon = "icons/edit.png",
                description = "Edit project",
                handler = { }
              ), 20),
              Button().configure(FxAction(
                icon = "icons/build.png",
                description = "Build project",
                handler = { }
              ), 20),
              Button().configure(FxAction(
                icon = "icons/run.png",
                description = "Run project",
                handler = { }
              ), 20),
              Button().configure(FxAction(
                icon = "icons/monitor.png",
                description = "Monitor project",
                handler = { }
              ), 20)
            )
            buttons.forEach { it.isFocusTraversable = false }
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
}