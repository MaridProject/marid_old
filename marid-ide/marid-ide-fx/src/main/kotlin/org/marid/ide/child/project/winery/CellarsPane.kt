package org.marid.ide.child.project.winery

import javafx.scene.Group
import javafx.scene.control.ScrollPane
import javafx.scene.shape.Rectangle
import org.marid.ide.extensions.bean
import org.marid.ide.project.Project
import org.springframework.beans.factory.ObjectFactory
import org.springframework.stereotype.Component

@Component
class CellarsPane(projectFactory: ObjectFactory<Project>) : ScrollPane() {

  private val project = projectFactory.bean
  private val stackPane = Group(Rectangle(3000.0, 3000.0))

  init {
    content = stackPane
  }
}