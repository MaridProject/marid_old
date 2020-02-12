package org.marid.ide.child.project.winery

import javafx.scene.control.Accordion
import javafx.scene.control.SplitPane
import javafx.scene.control.TitledPane
import org.marid.fx.i18n.localized
import org.springframework.stereotype.Component

@Component
class RacksPane(
  racksTable: RacksTable,
  rackArgumentList: RackArgumentList,
  rackInputList: RackInputList,
  rackInitializerList: RackInitializerList,
  rackDestroyerList: RackDestroyerList
) : SplitPane(
  racksTable,
  Accordion(
    TitledPane(null, rackArgumentList).also {
      it.textProperty().bind("Rack arguments".localized)
    },
    TitledPane(null, rackInputList).also {
      it.textProperty().bind("Rack inputs".localized)
    },
    TitledPane(null, rackInitializerList).also {
      it.textProperty().bind("Rack initializers".localized)
    },
    TitledPane(null, rackDestroyerList).also {
      it.textProperty().bind("Rack destroyers".localized)
    }
  ).also {
    it.expandedPane = it.panes[0]
  }
)