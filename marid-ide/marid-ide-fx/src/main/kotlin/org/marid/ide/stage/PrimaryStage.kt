package org.marid.ide.stage

import javafx.scene.Scene
import javafx.stage.Stage
import org.marid.ide.main.IdePane
import org.marid.image.MaridIconFx
import org.springframework.beans.factory.annotation.Autowired

data class PrimaryStage(val stage: Stage) {
  init {
    stage.title = "Marid IDE"
    stage.isMaximized = true
    stage.icons.addAll(MaridIconFx.getIcons(22, 24, 32))
  }

  @Autowired
  fun initScene(pane: IdePane) {
    stage.scene = Scene(pane)
  }
}