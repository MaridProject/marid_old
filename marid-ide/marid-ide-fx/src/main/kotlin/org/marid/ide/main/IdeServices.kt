package org.marid.ide.main

import javafx.beans.binding.Bindings
import javafx.beans.binding.DoubleBinding
import javafx.collections.FXCollections.observableArrayList
import javafx.collections.ObservableList
import javafx.concurrent.Service
import javafx.concurrent.Worker
import javafx.concurrent.WorkerStateEvent
import javafx.concurrent.WorkerStateEvent.*
import javafx.event.EventHandler
import javafx.scene.control.ProgressIndicator.INDETERMINATE_PROGRESS
import org.marid.fx.extensions.*
import org.springframework.stereotype.Component
import java.math.BigInteger
import java.util.*
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
      it.titleProperty(),
      it.runningProperty(),
      it.messageProperty()
    )
  }

  val services: ServiceList get() = servicesList.unmodified
  val runningServices: ServiceList get() = servicesList.filtered { it.isRunning }.unmodified
  val servicesText = "[%d / %d]".bindFormat(runningServices.bindSize, services.bindSize)

  val progress: DoubleBinding = Bindings.createDoubleBinding(Callable {
    runningServices.map(this::calcProgress).run {
      if (contains(INDETERMINATE_PROGRESS)) INDETERMINATE_PROGRESS else average()
    }
  }, runningServices)

  private fun id(service: Worker<*>) = Base64.getMimeEncoder().withoutPadding().encodeToString(BigInteger
    .valueOf(System.identityHashCode(service).toLong())
    .toByteArray()
  )

  private val stateEventHandler = EventHandler<WorkerStateEvent> {
    when (it.eventType) {
      WORKER_STATE_FAILED -> logger.ERROR("{0}: Unhandled error", it.source.exception, id(it.source))
      WORKER_STATE_SUCCEEDED -> logger.INFO("{0}: Succeeded", id(it.source))
      WORKER_STATE_CANCELLED -> logger.INFO("{0}: Cancelled", id(it.source))
      WORKER_STATE_READY -> logger.INFO("{0}: Ready", id(it.source))
      WORKER_STATE_RUNNING -> logger.INFO("{0}: Running", id(it.source))
      WORKER_STATE_SCHEDULED -> logger.INFO("{0}: Scheduled", id(it.source))
      else -> logger.INFO("{0} {1}", it.source.title, id(it.source))
    }
  }

  fun add(service: Service<*>) = if (service.state == Worker.State.READY) {
    servicesList.add(service)
    service.addEventFilter(ANY, stateEventHandler)
    true
  } else {
    false
  }

  fun remove(service: Service<*>) = if (service.isRunning) {
    val eh = object : EventHandler<WorkerStateEvent> {
      override fun handle(event: WorkerStateEvent?) {
        servicesList.remove(service)
        service.removeEventHandler(ANY, stateEventHandler)
        service.removeEventHandler(WORKER_STATE_CANCELLED, this)
        service.removeEventHandler(WORKER_STATE_FAILED, this)
        service.removeEventHandler(WORKER_STATE_SUCCEEDED, this)
      }
    }
    service.addEventHandler(WORKER_STATE_CANCELLED, eh)
    service.addEventHandler(WORKER_STATE_FAILED, eh)
    service.addEventHandler(WORKER_STATE_SUCCEEDED, eh)
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

