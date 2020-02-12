package org.marid.ide.child.project.winery

import javafx.scene.control.SplitPane
import javafx.scene.control.TitledPane
import org.marid.fx.i18n.localized
import org.springframework.stereotype.Component

@Component
class ConstantsPane(
  constantsTable: ConstantsTable,
  constantArgumentList: ConstantArgumentList
) : SplitPane(
  constantsTable,
  TitledPane(null, constantArgumentList).also {
    it.textProperty().bind("Arguments".localized)
    it.isCollapsible = false
    it.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE)
  }
) {
  init {
    setDividerPositions(0.5)
  }
}