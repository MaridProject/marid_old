package org.marid.fx.extensions

import javafx.beans.Observable
import javafx.concurrent.Worker
import javafx.scene.image.Image

fun Worker.State.icon(width: Int = 0, height: Int = 0): Image = when (this) {
    Worker.State.CANCELLED -> Image("icons/worker/cancelled.png", width.toDouble(), height.toDouble(), true, true)
    Worker.State.FAILED -> Image("icons/worker/failed.png", width.toDouble(), height.toDouble(), true, true)
    Worker.State.SUCCEEDED -> Image("icons/worker/success.png", width.toDouble(), height.toDouble(), true, true)
    Worker.State.READY -> Image("icons/worker/ready.png", width.toDouble(), height.toDouble(), true, true)
    Worker.State.RUNNING -> Image("icons/worker/running.png", width.toDouble(), height.toDouble(), true, true)
    Worker.State.SCHEDULED -> Image("icons/worker/scheduled.png", width.toDouble(), height.toDouble(), true, true)
  }

val Worker<*>.observables: Array<Observable> get() = arrayOf(
  stateProperty(),
  valueProperty(),
  workDoneProperty(),
  totalWorkProperty(),
  progressProperty(),
  titleProperty(),
  runningProperty(),
  messageProperty()
)