package org.marid.ide.child.project

import javafx.scene.control.Tab
import javafx.scene.control.TableView
import javafx.scene.control.ToolBar
import javafx.scene.layout.BorderPane
import org.marid.fx.action.Fx
import org.marid.fx.action.configure
import org.marid.fx.action.toolButton
import org.marid.fx.extensions.column
import org.marid.ide.extensions.bean
import org.marid.ide.project.Project
import org.springframework.beans.factory.ObjectFactory
import org.springframework.stereotype.Component

@Component
class DependenciesTab(contents: DependenciesTabContents) : Tab(null, contents) {
  init {
    isClosable = false
    configure(Fx(text = "Dependencies", icon = "icons/dependency.png"))
  }
}

@Component
class DependenciesTabContents(projectFactory: ObjectFactory<Project>) : BorderPane() {

  private val project = projectFactory.bean
  private val toolbar = ToolBar(
    Fx(
      text = "Add standard libraries",
      icon = "icons/standard.png",
      handler = { project.dependencies.loadDefault() }
    ).toolButton
  )
  private val list = TableView(project.dependencies.items).apply {
    column(350, "group") { it.group }
    column(300, "artifact") { it.artifact }
    column(200, "version") { it.version }
  }

  init {
    top = toolbar
    center = list
  }
}