package org.marid.ide.child.project.winery

import javafx.scene.control.SplitPane
import org.springframework.stereotype.Component

@Component
class CellarsPane(
  cellarsList: CellarsList,
  racksAndConstantsPane: RacksAndConstantsPane
) : SplitPane(cellarsList, racksAndConstantsPane) {

  init {
    setDividerPositions(0.1)
  }
}