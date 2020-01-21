package org.marid.ide.main

import javafx.beans.binding.Bindings
import javafx.beans.binding.DoubleBinding
import javafx.collections.FXCollections.observableArrayList
import javafx.collections.FXCollections.unmodifiableObservableList
import javafx.collections.ObservableList
import javafx.concurrent.Service
import javafx.concurrent.Worker
import javafx.concurrent.WorkerStateEvent
import javafx.event.EventHandler
import javafx.scene.control.ProgressIndicator.INDETERMINATE_PROGRESS
import org.marid.fx.extensions.bindFormat
import org.marid.fx.extensions.bindSize
import org.springframework.stereotype.Component
import java.util.concurrent.Callable

typealias ServiceList = ObservableList<Service<*>>

@Component
class IdeServices {

  private val servicesList: ServiceList = observableArrayList {
    arrayOf(
      it.stateProperty(),
      it.valueProperty(),
      it.workDoneProperty(),
      it.totalWorkProperty(),
      it.progressProperty(),
      it.titleProperty()
    )
  }

  val services: ServiceList get() = unmodifiableObservableList(servicesList)
  val runningServices: ServiceList get() = unmodifiableObservableList(servicesList.filtered { it.isRunning })
  val servicesText = "[%d / %d]".bindFormat(runningServices.bindSize, services.bindSize)

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
    val eh = object : EventHandler<WorkerStateEvent> {
      override fun handle(event: WorkerStateEvent?) {
        servicesList.remove(service)
        service.removeEventHandler(WorkerStateEvent.WORKER_STATE_CANCELLED, this)
        service.removeEventHandler(WorkerStateEvent.WORKER_STATE_FAILED, this)
        service.removeEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, this)
      }
    }
    service.addEventHandler(WorkerStateEvent.WORKER_STATE_CANCELLED, eh)
    service.addEventHandler(WorkerStateEvent.WORKER_STATE_FAILED, eh)
    service.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, eh)
    service.cancel()
    Thread.yield()
    !service.isRunning
  } else {
    servicesList.remove(service)
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

