package org.marid.ide.child.project

import javafx.scene.control.Tab
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.ToolBar
import javafx.scene.layout.BorderPane
import javafx.util.Callback
import org.marid.fx.extensions.readOnlyProp
import org.marid.fx.i18n.localized
import org.marid.ide.extensions.bean
import org.marid.ide.project.Project
import org.marid.ide.project.xml.XmlRepository
import org.springframework.beans.factory.ObjectFactory
import org.springframework.stereotype.Component

@Component
class RepositoriesTab(contents: RepositoriesTabContents) : Tab(null, contents) {
  init {
    isClosable = false
    textProperty().bind("Repositories".localized)
  }
}

@Component
class RepositoriesTabContents(projectFactory: ObjectFactory<Project>) : BorderPane() {

  private val project = projectFactory.bean
  private val toolbar = ToolBar()
  private val list = TableView(project.repositories.items).apply {
    columns += listOf(
      TableColumn<XmlRepository, String>().apply {
        minWidth = 300.0; prefWidth = 300.0; maxWidth = 360.0
        textProperty().bind("Id".localized)
        cellValueFactory = Callback { it.value.id.readOnlyProp }
        style = "-fx-font-family: monospaced"
      },
      TableColumn<XmlRepository, String>().apply {
        minWidth = 128.0; prefWidth = 160.0; maxWidth = 200.0
        textProperty().bind("Name".localized)
        cellValueFactory = Callback { it.value.name }
      },
      TableColumn<XmlRepository, String>("URL").apply {
        minWidth = 200.0; prefWidth = 250.0; maxWidth = 1200.0
        cellValueFactory = Callback { it.value.url }
      }
    )
    columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
  }

  init {
    top = toolbar
    center = list
  }
}