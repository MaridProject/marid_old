package org.marid.ide.main

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.shape.Circle
import org.marid.ide.IdeLog
import org.marid.ide.extensions.color
import org.marid.ide.extensions.map
import org.springframework.stereotype.Component

@Component
class IdeStatusBar(
  private val ideLog: IdeLog
) : HBox(4.0) {

  init {
    isFillHeight = true
    alignment = Pos.CENTER_LEFT
  }

  private val statusText = Label("Done")
    .also { children += it }
    .also { setHgrow(it, Priority.ALWAYS) }
    .also { it.maxWidth = Double.MAX_VALUE }
    .also { setMargin(it, Insets(5.0)) }
    .also { it.graphic = Circle(5.0).also { c -> c.fillProperty().bind(ideLog.lastLevel.map { l -> l.color }) } }
    .also { it.graphicTextGap = 5.0 }

  private val progressBar = ProgressBar(0.0)
    .also { children += it }
    .also { setHgrow(it, Priority.NEVER) }
    .also { it.minWidth = 100.0 }
    .also { setMargin(it, Insets(5.0)) }
}