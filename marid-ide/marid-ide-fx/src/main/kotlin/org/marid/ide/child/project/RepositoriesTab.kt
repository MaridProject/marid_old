package org.marid.ide.child.project

import javafx.scene.control.Tab
import javafx.scene.control.TableView
import javafx.scene.control.ToolBar
import javafx.scene.layout.BorderPane
import org.marid.fx.i18n.localized
import org.marid.ide.project.xml.XmlRepository
import org.springframework.stereotype.Component

@Component
class RepositoriesTab(contents: RepositoriesTabContents) : Tab(null, contents) {
  init {
    isClosable = false
    textProperty().bind("Repositories".localized)
  }
}

@Component
class RepositoriesTabContents : BorderPane() {

  private val toolbar = ToolBar()
  private val list = TableView<XmlRepository>()

  init {
    top = toolbar
    center = list
  }
}