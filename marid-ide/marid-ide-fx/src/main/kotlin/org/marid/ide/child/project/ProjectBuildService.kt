package org.marid.ide.child.project

import javafx.application.Platform
import javafx.beans.InvalidationListener
import javafx.concurrent.Service
import javafx.concurrent.Task
import javafx.concurrent.Worker.State.*
import org.marid.fx.extensions.logger
import org.marid.ide.common.IdeProperties
import org.marid.ide.extensions.bean
import org.marid.ide.main.IdeServices
import org.marid.ide.project.Project
import org.springframework.beans.factory.ObjectFactory
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Component
class ProjectBuildService(
  private val services: IdeServices,
  private val properties: IdeProperties,
  projectFactory: ObjectFactory<Project>
) : Service<Unit>() {

  private val project = projectFactory.bean
  private val projectInvalidationListener = InvalidationListener { invalidated() }
  private var dirty = false

  override fun createTask(): Task<Unit> {
    return object : Task<Unit>() {
      override fun call() {
        project.logger.info("Build started")
        project.logger.info("Build finished")
      }
    }
  }

  @PostConstruct
  fun onInit() {
    services.add(this)
    project.observables.forEach { it.addListener(projectInvalidationListener) }
    stateProperty().addListener { _, _, s ->
      if (s == SUCCEEDED || s == FAILED || s == CANCELLED) {
        if (dirty) {
          dirty = false
          Platform.runLater { restart() }
        }
      }
    }
    start()
  }

  @PreDestroy
  fun onDestroy() {
    services.remove(this)
    project.observables.forEach { it.removeListener(projectInvalidationListener) }
  }

  private fun invalidated() {
    if (isRunning) {
      dirty = true
    } else {
      Platform.runLater { restart() }
    }
  }
}