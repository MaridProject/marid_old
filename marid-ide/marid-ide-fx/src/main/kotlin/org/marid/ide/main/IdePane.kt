package org.marid.ide.main

import javafx.scene.layout.BorderPane
import org.springframework.stereotype.Component

@Component
class IdePane(
  tabs: IdeTabs,
  menu: IdeMenuBar,
  status: IdeStatusBar
) : BorderPane(tabs, menu, null, status, null) {
}