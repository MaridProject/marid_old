package org.marid.ide.main

import javafx.beans.binding.Bindings
import javafx.beans.binding.DoubleBinding
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.concurrent.Service
import javafx.concurrent.Worker
import javafx.scene.control.ProgressIndicator.INDETERMINATE_PROGRESS
import org.springframework.stereotype.Component
import java.util.concurrent.Callable

@Component
class IdeServices {

  private val servicesList: ObservableList<Service<*>> = FXCollections.observableArrayList {
    arrayOf(
      it.stateProperty(),
      it.valueProperty(),
      it.workDoneProperty(),
      it.totalWorkProperty(),
      it.progressProperty(),
      it.titleProperty()
    )
  }

  val services: ObservableList<Service<*>> get() = FXCollections.unmodifiableObservableList(servicesList)

  val progress: DoubleBinding = Bindings.createDoubleBinding(Callable {
    val runningServices = servicesList.filter { it.isRunning }
    val progresses = runningServices.map(this::calcProgress)
    if (progresses.contains(INDETERMINATE_PROGRESS)) INDETERMINATE_PROGRESS else progresses.average()
  }, servicesList)

  fun add(service: Service<*>) = if (service.state == Worker.State.READY) {
    servicesList.add(service)
    true
  } else {
    false
  }

  fun remove(service: Service<*>) = if (service.isRunning) {
    false
  } else {
    services.remove(service)
    true
  }

  private fun calcProgress(service: Service<*>): Double {
    val total = service.totalWork
    val work = service.workDone
    val progress = service.progress
    return if (work != INDETERMINATE_PROGRESS && total >= 0.0)
      work / total
    else if (progress != INDETERMINATE_PROGRESS)
      progress
    else
      INDETERMINATE_PROGRESS
  }
}

