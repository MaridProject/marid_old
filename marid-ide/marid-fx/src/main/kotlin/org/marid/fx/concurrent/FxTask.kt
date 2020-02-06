package org.marid.fx.concurrent

import javafx.concurrent.Task

abstract class FxTask<T> : Task<T>() {
  fun updateProgress(progress: Double) = updateProgress(progress, 1.0)
}