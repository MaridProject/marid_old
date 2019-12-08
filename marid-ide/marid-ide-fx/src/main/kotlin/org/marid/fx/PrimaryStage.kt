package org.marid.fx

import javafx.embed.swing.SwingFXUtils
import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import org.marid.image.MaridIcon
import java.awt.Color
import java.util.*
import java.util.stream.Collectors

data class PrimaryStage(val stage: Stage) {
  init {
    stage.title = "Marid IDE"
    stage.isMaximized = true
    stage.icons.addAll(icons(16, 24, 32, 48))
    stage.scene = Scene(BorderPane())
  }

  fun icons(vararg sizes: Int) = Arrays.stream(sizes)
    .parallel()
    .mapToObj { size -> MaridIcon.getImage(size, Color.GREEN) }
    .map { img -> SwingFXUtils.toFXImage(img, null) }
    .collect(Collectors.toList())
}