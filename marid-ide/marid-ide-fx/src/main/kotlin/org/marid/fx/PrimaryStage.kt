package org.marid.fx

import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import org.marid.image.MaridIconFx

data class PrimaryStage(val stage: Stage) {
  init {
    stage.title = "Marid IDE"
    stage.isMaximized = true
    stage.icons.addAll(MaridIconFx.getIcons(22, 24, 32))
    stage.scene = Scene(BorderPane(Button("xxx")))
  }
}