package org.marid.ide.child.project.winery

import javafx.scene.control.TableView
import org.marid.fx.extensions.column
import org.marid.fx.extensions.readOnlyProp
import org.marid.ide.common.IdePreferences
import org.marid.ide.project.model.ArgumentWrapper
import org.springframework.stereotype.Component

@Component
class RackArgumentList(idePreferences: IdePreferences) : TableView<ArgumentWrapper>() {

  init {
    columnResizePolicy = CONSTRAINED_RESIZE_POLICY

    column(400, "Argument") { "".readOnlyProp }
    column(200, "Type") { it.argumentType }.also { it.visibleProperty().bind(idePreferences.showTypes) }
  }
}