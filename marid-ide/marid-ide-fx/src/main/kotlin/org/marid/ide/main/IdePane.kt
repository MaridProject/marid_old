package org.marid.ide.main

import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import org.marid.spring.beans.InternalBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class IdePane(
  tabs: IdeTabs,
  menu: IdeMenuBar,
  status: IdeStatusBar
) : BorderPane(tabs, menu, null, status, null) {

  @Autowired
  private fun initScene(primaryStage: InternalBean<Stage>) {
    primaryStage.bean.scene = Scene(this, 1024.0, 768.0);
  }
}