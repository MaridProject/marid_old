package org.marid.ide.child.project

import javafx.application.Platform
import javafx.beans.InvalidationListener
import javafx.concurrent.Service
import javafx.concurrent.Task
import javafx.concurrent.Worker.State.*
import org.eclipse.aether.artifact.DefaultArtifact
import org.eclipse.aether.repository.RemoteRepository
import org.eclipse.aether.resolution.ArtifactRequest
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
        project.withSession { session, system ->
          val repos = project.repositories.items
            .map {
              RemoteRepository.Builder(it.name.get(), "default", it.url.get())
                .build()
            }
          val artifactRequests = project.dependencies.items
            .map {
              val artifact = DefaultArtifact(
                it.group.get(),
                it.artifact.get(), "jar",
                properties.substitute(it.version.get())
              )
              ArtifactRequest(artifact, repos, "")
            }
          val results = system.resolveArtifacts(session, artifactRequests)
        }
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