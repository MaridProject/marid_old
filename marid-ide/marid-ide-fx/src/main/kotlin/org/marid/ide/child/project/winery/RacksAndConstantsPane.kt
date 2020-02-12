package org.marid.ide.child.project.winery

import javafx.geometry.Orientation
import javafx.scene.control.SplitPane
import org.springframework.stereotype.Component

@Component
class RacksAndConstantsPane(
  racksTable: RacksTable,
  constantsTable: ConstantsTable
) : SplitPane(racksTable, constantsTable) {
  init {
    setDividerPositions(0.7)
    orientation = Orientation.VERTICAL
  }
}