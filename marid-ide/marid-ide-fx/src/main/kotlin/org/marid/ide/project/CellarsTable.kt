package org.marid.ide.project

import javafx.scene.control.TableView
import org.marid.fx.extensions.bindSize
import org.marid.fx.extensions.column
import org.marid.ide.project.model.FxCellar
import org.springframework.stereotype.Component

@Component
class CellarsTable : TableView<FxCellar>() {

  init {
    columnResizePolicy = CONSTRAINED_RESIZE_POLICY
    maxHeight = 300.0

    column(300, "Name") { it.name }
    column(100, "Racks") { it.racks.bindSize }
    column(100, "Constants") { it.constants.bindSize }
  }

}