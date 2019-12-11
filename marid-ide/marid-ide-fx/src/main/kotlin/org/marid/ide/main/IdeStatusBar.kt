package org.marid.ide.main

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import org.springframework.stereotype.Component

@Component
class IdeStatusBar : HBox(4.0) {

  init {
    isFillHeight = true
    alignment = Pos.CENTER_LEFT
  }

  private val statusText = Label("Done")
    .also { children += it }
    .also { setHgrow(it, Priority.ALWAYS) }
    .also { it.maxWidth = Double.MAX_VALUE }
    .also { setMargin(it, Insets(5.0)) }
    .also { it.graphic = Circle(5.0, Color.GREEN) }
    .also { it.graphicTextGap = 5.0 }

  private val progressBar = ProgressBar(0.0)
    .also { children += it }
    .also { setHgrow(it, Priority.NEVER) }
    .also { it.minWidth = 100.0 }
    .also { setMargin(it, Insets(5.0)) }
}