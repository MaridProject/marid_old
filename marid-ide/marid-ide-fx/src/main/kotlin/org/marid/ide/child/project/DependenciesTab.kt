package org.marid.ide.child.project

import javafx.scene.control.Tab
import javafx.scene.control.TableView
import javafx.scene.control.ToolBar
import javafx.scene.layout.BorderPane
import org.marid.fx.action.Fx
import org.marid.fx.action.configure
import org.marid.fx.extensions.column
import org.marid.ide.extensions.bean
import org.marid.ide.project.Project
import org.marid.ide.project.xml.XmlDependency
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
  private val toolbar = ToolBar()
  private val list = TableView(project.dependencies.items).apply {
    column<XmlDependency, String>(150, "group") { it.value.group }
    column<XmlDependency, String>(150, "artifact") { it.value.artifact }
    column<XmlDependency, String>(150, "version") { it.value.version }
  }

  init {
    top = toolbar
    center = list
  }
}