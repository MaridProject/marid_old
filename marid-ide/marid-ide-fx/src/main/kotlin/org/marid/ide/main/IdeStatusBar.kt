package org.marid.ide.main

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
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
    .also { setHgrow(it, Priority.ALWAYS) }
    .apply { maxWidth = Double.MAX_VALUE }
    .apply { textProperty().bind(ideLog.records.bindString { it.last().formatSafe.replace('\n', ' ') }) }
    .apply { graphic = Circle(5.0).apply { fillProperty().bind(ideLog.lastLevel.map { it?.color }) } }
    .apply { graphicTextGap = 5.0 }

  private val progressBar = ProgressBar(0.0)
    .also { setHgrow(it, Priority.NEVER) }
    .apply { minWidth = 100.0 }
    .apply { progressProperty().bind(ideServices.progress) }

  private val servicesCountLabel = Button()
    .also { setHgrow(it, Priority.NEVER) }
    .apply { textProperty().bind(ideServices.servicesText) }
    .apply { disableProperty().bind(ideServices.services.bindEmpty) }

  init {
    children += listOf(
      statusText,
      progressBar,
      servicesCountLabel
    )
    children.forEach {
      setMargin(it, Insets(5.0))
    }
  }
}