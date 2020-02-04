package org.marid.ide.child.project

import javafx.application.Platform
import javafx.beans.InvalidationListener
import javafx.concurrent.Service
import javafx.concurrent.Task
import javafx.concurrent.Worker.State.*
import org.eclipse.aether.artifact.DefaultArtifact
import org.eclipse.aether.collection.CollectRequest
import org.eclipse.aether.graph.Dependency
import org.eclipse.aether.graph.DependencyFilter
import org.eclipse.aether.repository.RemoteRepository
import org.eclipse.aether.resolution.DependencyRequest
import org.marid.fx.extensions.deleteDirectoryContents
import org.marid.fx.extensions.toImmutableMap
import org.marid.fx.extensions.toTypedArray
import org.marid.ide.common.IdeProperties
import org.marid.ide.extensions.bean
import org.marid.ide.main.IdeServices
import org.marid.ide.project.Project
import org.marid.ide.project.ProjectDependencyResolver
import org.springframework.beans.factory.ObjectFactory
import org.springframework.stereotype.Component
import java.net.URLClassLoader
import java.nio.file.Files
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Component
class ProjectBuildService(
  private val services: IdeServices,
  private val properties: IdeProperties,
  private val dependencyResolver: ProjectDependencyResolver,
  projectFactory: ObjectFactory<Project>
) : Service<Unit>() {

  private val project = projectFactory.bean
  private val projectInvalidationListener = InvalidationListener { invalidated() }
  private var dirty = false
  private val dependencyFilter = DependencyFilter { _, _ -> true }
  private var classLoader: URLClassLoader? = null

  override fun createTask(): Task<Unit> {
    return object : Task<Unit>() {
      override fun call() {
        project.logger.info("Build started")
        dependencyResolver.withSession { session, system ->
          val repos = project.repositories.items
            .map {
              RemoteRepository.Builder(it.name.get(), "default", it.url.get())
                .build()
            }
          val dependencies = project.dependencies.items
            .map { (g, a, v) -> DefaultArtifact(g, a, "", "jar", properties.substitute(v)) }
            .map { Dependency(it, "runtime") }
          val collectRequest = CollectRequest(null as Dependency?, dependencies, repos)
          val result = system.resolveDependencies(session, DependencyRequest(collectRequest, dependencyFilter))
          if (result.collectExceptions.isNotEmpty()) {
            throw result.collectExceptions.reduce { e1, e2 -> e1.apply { addSuppressed(e2) } }
          }
          val artifacts = result.artifactResults.parallelStream()
            .map { it.artifact }
            .toImmutableMap(
              { it.groupId to it.artifactId },
              { it },
              { a, b -> maxOf(a, b, compareBy { it.version }) }
            ).values
          project.withWrite {
            project.depsDirectory.deleteDirectoryContents()
            val urls = artifacts.parallelStream()
              .map { it.file.toPath() to project.depsDirectory.resolve(it.file.name) }
              .peek { (from, to) -> Files.copy(from, to) }
              .map { (_, to) -> to.toUri().toURL() }
              .toTypedArray()
            classLoader?.also { it.close() }
            classLoader = URLClassLoader(urls, ClassLoader.getPlatformClassLoader())
            project.save()
          }
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
    classLoader?.close()
  }

  private fun invalidated() {
    if (isRunning) {
      dirty = true
    } else {
      Platform.runLater { restart() }
    }
  }
}