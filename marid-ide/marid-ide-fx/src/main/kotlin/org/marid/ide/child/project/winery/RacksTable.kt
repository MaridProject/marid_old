package org.marid.ide.child.project.winery

import javafx.scene.control.TableView
import org.marid.fx.extensions.column
import org.marid.ide.common.IdePreferences
import org.marid.ide.project.model.RackWrapper
import org.springframework.stereotype.Component

@Component
class RacksTable(idePreferences: IdePreferences) : TableView<RackWrapper>() {

  init {
    columnResizePolicy = CONSTRAINED_RESIZE_POLICY

    column(200, "Name") { it.name }
    column(200, "Factory") { it.factory }
    column(400, "Type") { it.type }.also { it.visibleProperty().bind(idePreferences.showTypes) }
  }
}