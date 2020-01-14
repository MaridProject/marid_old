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
import org.marid.ide.project.xml.XmlRepository
import org.springframework.beans.factory.ObjectFactory
import org.springframework.stereotype.Component

@Component
class RepositoriesTab(contents: RepositoriesTabContents) : Tab(null, contents) {
  init {
    isClosable = false
    configure(Fx(text = "Repositories", icon = "icons/repository.png"))
  }
}

@Component
class RepositoriesTabContents(projectFactory: ObjectFactory<Project>) : BorderPane() {

  private val project = projectFactory.bean
  private val toolbar = ToolBar()
  private val list = TableView(project.repositories.items).apply {
    column<XmlRepository, String>(160, "Name") { it.value.name }
    column<XmlRepository, String>(250, "URL") { it.value.url }
    columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
  }

  init {
    top = toolbar
    center = list
  }
}