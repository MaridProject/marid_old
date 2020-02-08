package org.marid.ide.main

import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import javafx.scene.control.ToggleButton
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.shape.Circle
import javafx.stage.Window
import org.marid.fx.extensions.bindEmpty
import org.marid.fx.extensions.color
import org.marid.fx.extensions.map
import org.marid.ide.extensions.bean
import org.marid.ide.log.IdeLog
import org.springframework.beans.factory.ObjectFactory
import org.springframework.stereotype.Component

@Component
class IdeStatusBar(
  private val ideLog: IdeLog,
  private val ideServices: IdeServices,
  private val ideServicesWindowFactory: ObjectFactory<IdeServicesWindow>
) : HBox(4.0) {

  init {
    isFillHeight = true
    alignment = Pos.CENTER_LEFT
  }

  private val statusText = Label()
    .also { setHgrow(it, Priority.ALWAYS) }
    .apply { maxWidth = Double.MAX_VALUE }
    .apply { graphic = Circle(5.0).apply { fillProperty().bind(ideLog.lastLevel.map { it?.color }) } }
    .apply { graphicTextGap = 5.0 }
    .apply { textProperty().bind(ideServices.lastMessage) }

  private val progressBar = ProgressBar(0.0)
    .also { setHgrow(it, Priority.NEVER) }
    .apply { minWidth = 100.0 }
    .apply { progressProperty().bind(ideServices.progress) }

  private val servicesCountLabel = ToggleButton()
    .also { setHgrow(it, Priority.NEVER) }
    .apply {
      isFocusTraversable = false
      textProperty().bind(ideServices.servicesText)
      disableProperty().bind(ideServices.services.bindEmpty)
      onAction = EventHandler {
        when (val w = Window.getWindows().find { it is IdeServicesWindow }) {
          null -> ideServicesWindowFactory.bean
          else -> {
            w.hide()
          }
        }
      }
    }

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