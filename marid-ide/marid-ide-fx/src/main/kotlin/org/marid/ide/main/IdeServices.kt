package org.marid.ide.main

import javafx.beans.binding.Bindings
import javafx.beans.binding.DoubleBinding
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ChangeListener
import javafx.collections.FXCollections.observableArrayList
import javafx.collections.ObservableList
import javafx.concurrent.Service
import javafx.concurrent.Worker
import javafx.concurrent.WorkerStateEvent
import javafx.concurrent.WorkerStateEvent.*
import javafx.event.EventHandler
import javafx.scene.control.ProgressIndicator.INDETERMINATE_PROGRESS
import org.marid.fx.extensions.*
import org.marid.fx.i18n.i18n
import org.springframework.stereotype.Component
import java.math.BigInteger
import java.util.*
import java.util.concurrent.Callable
import java.util.logging.Level

typealias ServiceList = ObservableList<Service<*>>

@Component
class IdeServices {

  private val servicesList: ServiceList = observableArrayList { it.observables }

  val services: ServiceList get() = servicesList.unmodified
  val runningServices: ServiceList get() = servicesList.filtered { it.isRunning }.unmodified
  val servicesText = "[%d / %d]".bindFormat(runningServices.bindSize, services.bindSize)
  val lastMessage = SimpleStringProperty(this, "lastMessage", "Marid")

  val progress: DoubleBinding = Bindings.createDoubleBinding(Callable {
    runningServices.map(this::calcProgress).run {
      if (contains(INDETERMINATE_PROGRESS)) INDETERMINATE_PROGRESS else average()
    }
  }, runningServices)

  fun id(service: Worker<*>): String = Base64.getMimeEncoder().withoutPadding().encodeToString(BigInteger
    .valueOf(System.identityHashCode(service).toLong())
    .toByteArray()
  )

  private val stateEventHandler = EventHandler<WorkerStateEvent> {
    val level = if (it.eventType == WORKER_STATE_FAILED) Level.SEVERE else Level.INFO
    val id = id(it.source)
    logger.LOG(level, "{0}: {1}", it.source.exception, id, it.eventType.name)
    lastMessage.set("$id: ${it.eventType.name.i18n()}")
  }

  private val messageHandler = ChangeListener<String> { _, _, n -> lastMessage.set(n) }

  fun add(service: Service<*>) = if (service.state == Worker.State.READY) {
    servicesList.add(service)
    service.addEventFilter(ANY, stateEventHandler)
    service.messageProperty().addListener(messageHandler)
    true
  } else {
    false
  }

  fun remove(service: Service<*>) = if (service.isRunning) {
    val eh = object : EventHandler<WorkerStateEvent> {
      override fun handle(event: WorkerStateEvent?) {
        servicesList.remove(service)
        service.messageProperty().removeListener(messageHandler)
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

