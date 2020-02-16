package org.marid.ide.project

import javafx.geometry.Orientation
import javafx.scene.control.SplitPane
import org.springframework.stereotype.Component

@Component
class CellarsPane(cellarsTable: CellarsTable, cellarPane: CellarPane) : SplitPane(cellarsTable, cellarPane) {

  init {
    orientation = Orientation.VERTICAL
    setDividerPositions(0.4)
  }
}