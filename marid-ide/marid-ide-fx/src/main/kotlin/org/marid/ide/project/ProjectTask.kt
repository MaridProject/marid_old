package org.marid.ide.project

import javafx.application.Platform
import javafx.beans.value.ChangeListener
import org.marid.fx.concurrent.FxTask

abstract class ProjectTask<V>(val project: Project) : FxTask<V>() {

  private val progressListener = ChangeListener<Number> { _, _, v ->
    project.Friend().progressWrapper.set(v.toDouble())
  }

  final override fun call(): V {
    Platform.runLater { this.progressProperty().addListener(progressListener) }
    try {
      return callTask()
    } finally {
      Platform.runLater {
        updateProgress(0L, 100L)
        this.progressProperty().removeListener(progressListener)
      }
    }
  }

  abstract fun callTask(): V
}