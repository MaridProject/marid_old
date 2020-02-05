package org.marid.ide.project

import javafx.application.Platform
import javafx.beans.value.ChangeListener
import javafx.concurrent.Task

abstract class ProjectTask<V>(val project: Project) : Task<V>() {

  private val progressListener = ChangeListener<Number> { _, _, v -> project.Friend().progressWrapper.set(v.toDouble()) }

  final override fun call(): V {
    Platform.runLater { this.progressProperty().addListener(progressListener) }
    try {
      return callTask()
    } finally {
      Platform.runLater { this.progressProperty().removeListener(progressListener) }
    }
  }

  abstract fun callTask(): V
}