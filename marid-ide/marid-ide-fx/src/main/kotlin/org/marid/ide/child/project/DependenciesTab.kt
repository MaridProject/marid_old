package org.marid.ide.child.project

import javafx.scene.control.Tab
import javafx.scene.layout.BorderPane
import org.marid.fx.action.Fx
import org.marid.fx.action.configure
import org.springframework.stereotype.Component

@Component
class DependenciesTab(contents: DependenciesTabContents) : Tab(null, contents) {
  init {
    isClosable = false
    configure(Fx(text = "Dependencies", icon = "icons/dependency.png"))
  }
}

@Component
class DependenciesTabContents : BorderPane() {

}