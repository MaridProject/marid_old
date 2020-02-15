package org.marid.ide.child.project.winery

import javafx.scene.control.TableView
import org.marid.fx.extensions.column
import org.marid.ide.common.IdePreferences
import org.marid.ide.project.model.FxRack
import org.springframework.stereotype.Component

@Component
class RacksTable(idePreferences: IdePreferences) : TableView<FxRack>() {

  init {
    columnResizePolicy = CONSTRAINED_RESIZE_POLICY

    column(200, "Name") { it.name }
    column(400, "Type") { it.resolvedType }.also { it.visibleProperty().bind(idePreferences.showTypes) }
  }
}