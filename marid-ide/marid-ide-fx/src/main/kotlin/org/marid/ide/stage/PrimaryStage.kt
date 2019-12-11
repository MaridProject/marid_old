package org.marid.ide.stage

import javafx.scene.Scene
import javafx.stage.Stage
import org.marid.ide.main.IdePane
import org.marid.image.MaridIconFx
import org.marid.spring.annotation.InternalComponent
import org.springframework.beans.factory.annotation.Autowired

@InternalComponent
data class PrimaryStage(
  @Suppress("SpringJavaInjectionPointsAutowiringInspection") val stage: Stage
) {
  init {
    stage.title = "Marid IDE"
    stage.isMaximized = true
    stage.icons.addAll(MaridIconFx.getIcons(22, 24, 32))
  }

  @Autowired
  fun initScene(pane: IdePane) {
    stage.scene = Scene(pane, 1024.0, 768.0)
  }
}