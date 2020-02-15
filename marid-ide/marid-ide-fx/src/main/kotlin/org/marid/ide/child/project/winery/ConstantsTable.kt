package org.marid.ide.child.project.winery

import javafx.scene.control.TableView
import org.marid.fx.extensions.column
import org.marid.ide.common.IdePreferences
import org.marid.ide.project.model.FxCellarConstant
import org.springframework.stereotype.Component

@Component
class ConstantsTable(idePreferences: IdePreferences) : TableView<FxCellarConstant>() {

  init {
    columnResizePolicy = CONSTRAINED_RESIZE_POLICY

    column(300, "Factory") { it.factory }
    column(300, "Selector") { it.selector }
    column(300, "Name") { it.name }
    column(400, "Type") { it.resolvedType }.also { it.visibleProperty().bind(idePreferences.showTypes) }
  }
}