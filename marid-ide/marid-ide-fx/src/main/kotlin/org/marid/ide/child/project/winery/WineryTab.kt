package org.marid.ide.child.project.winery

import javafx.scene.control.Tab
import javafx.scene.layout.BorderPane
import org.marid.fx.action.Fx
import org.marid.fx.action.configure
import org.springframework.stereotype.Component

@Component
class WineryTab(cellarsPane: CellarsPane) : Tab(null, BorderPane(cellarsPane)) {
  init {
    isClosable = false
    configure(Fx(text = "Winery", icon = "icons/winery.png"))
  }
}