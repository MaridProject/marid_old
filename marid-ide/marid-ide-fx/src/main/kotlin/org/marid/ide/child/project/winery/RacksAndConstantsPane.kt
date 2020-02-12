package org.marid.ide.child.project.winery

import javafx.scene.control.Accordion
import javafx.scene.control.TitledPane
import org.marid.fx.i18n.localized
import org.springframework.stereotype.Component

@Component
class RacksAndConstantsPane(racksPane: RacksPane, constantsPane: ConstantsPane) : Accordion(
  TitledPane(null, racksPane).also {
    it.textProperty().bind("Racks".localized)
    it.isCollapsible = true
  },
  TitledPane(null, constantsPane).also {
    it.textProperty().bind("Constants".localized)
    it.isCollapsible = true
  }
) {
  init {
    expandedPane = panes[0]
  }
}