package org.marid.ide.child.project.winery

import javafx.scene.control.Tab
import javafx.scene.layout.BorderPane
import org.marid.fx.action.Fx
import org.marid.fx.action.configure
import org.springframework.stereotype.Component

@Component
class WineryTab(wineryPane: WineryPane) : Tab(null, BorderPane(wineryPane)) {
  init {
    isClosable = false
    configure(Fx(text = "Winery", icon = "icons/winery.png"))
  }
}