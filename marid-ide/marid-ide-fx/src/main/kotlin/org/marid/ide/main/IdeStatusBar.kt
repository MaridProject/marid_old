package org.marid.ide.main

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.shape.Circle
import org.marid.fx.extensions.*
import org.marid.ide.log.IdeLog
import org.springframework.stereotype.Component

@Component
class IdeStatusBar(
  private val ideLog: IdeLog,
  private val ideServices: IdeServices
) : HBox(4.0) {

  init {
    isFillHeight = true
    alignment = Pos.CENTER_LEFT
  }

  private val statusText = Label()
    .also { children += it }
    .also { setHgrow(it, Priority.ALWAYS) }
    .also { setMargin(it, Insets(5.0)) }
    .apply { maxWidth = Double.MAX_VALUE }
    .apply { textProperty().bind(ideLog.records.singleLined { it.message }.bindLast.mapString { it?.message }) }
    .apply { graphic = Circle(5.0).apply { fillProperty().bind(ideLog.lastLevel.map { it?.color }) } }
    .apply { graphicTextGap = 5.0 }

  private val progressBar = ProgressBar(0.0)
    .also { children += it }
    .also { setHgrow(it, Priority.NEVER) }
    .also { setMargin(it, Insets(5.0)) }
    .apply { minWidth = 100.0 }
    .apply { progressProperty().bind(ideServices.progress) }
}