package org.marid.ide.child.project.winery

import javafx.scene.control.ScrollPane
import javafx.scene.layout.StackPane
import javafx.scene.shape.Rectangle
import org.springframework.stereotype.Component

@Component
class CellarsPane : ScrollPane() {

  private val stackPane = StackPane(Rectangle(3000.0, 3000.0))

  init {
    content = stackPane
  }
}