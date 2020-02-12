package org.marid.ide.child.project.winery

import javafx.scene.control.SplitPane
import javafx.scene.control.TitledPane
import org.marid.fx.i18n.localized
import org.springframework.stereotype.Component

@Component
class WineryPane(
  cellarsList: CellarsList,
  racksAndConstantsPane: RacksAndConstantsPane
) : SplitPane(
  TitledPane(null, cellarsList)
    .also {
      it.textProperty().bind("Cellars".localized)
      it.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE)
      it.isExpanded = true
      it.isCollapsible = false
    },
  racksAndConstantsPane
) {

  init {
    setDividerPositions(0.1)
  }
}