package org.marid.ide.child.project.winery

import javafx.scene.control.TableView
import org.marid.fx.extensions.column
import org.marid.ide.common.IdePreferences
import org.marid.ide.project.model.InputWrapper
import org.springframework.stereotype.Component

@Component
class RackInputList(idePreferences: IdePreferences) : TableView<InputWrapper>() {

  init {
    columnResizePolicy = CONSTRAINED_RESIZE_POLICY

    column(200, "Name") { it.name }
    column(300, "Argument") { it.argument }
    column(300, "Type") { it.argument.get().argumentType }.also { it.visibleProperty().bind(idePreferences.showTypes) }
  }
}