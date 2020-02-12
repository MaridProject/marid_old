package org.marid.ide.child.project.winery

import javafx.scene.control.TableView
import org.marid.fx.extensions.column
import org.marid.ide.project.model.RackWrapper
import org.springframework.stereotype.Component

@Component
class RacksTable : TableView<RackWrapper>() {

  init {
    column(300, "Name") { it.name }
    column(400, "Factory") { it.factory }
  }
}