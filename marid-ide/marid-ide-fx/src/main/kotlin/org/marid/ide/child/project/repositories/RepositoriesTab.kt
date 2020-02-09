package org.marid.ide.child.project.repositories

import javafx.scene.control.Tab
import javafx.scene.control.TableRow
import javafx.scene.control.TableView
import javafx.scene.control.ToolBar
import javafx.scene.layout.BorderPane
import javafx.util.Callback
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
  private val list = TableView(project.repositories.items)
    .apply {
      column(160, "Name") { it.name }
      column(250, "URL") { it.url }
    }
    .apply { columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY }
    .apply {
      rowFactory = Callback {
        TableRow<XmlRepository>()
      }
    }

  init {
    top = toolbar
    center = list
  }
}