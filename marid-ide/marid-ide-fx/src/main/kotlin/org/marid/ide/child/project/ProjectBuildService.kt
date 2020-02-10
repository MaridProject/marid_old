package org.marid.ide.child.project

import javafx.beans.InvalidationListener
import javafx.concurrent.Service
import javafx.concurrent.Task
import javafx.concurrent.Worker.State.SUCCEEDED
import org.eclipse.aether.artifact.DefaultArtifact
import org.eclipse.aether.collection.CollectRequest
import org.eclipse.aether.graph.Dependency
import org.eclipse.aether.graph.DependencyFilter
import org.eclipse.aether.repository.RemoteRepository
import org.eclipse.aether.repository.RepositoryPolicy
import org.eclipse.aether.repository.RepositoryPolicy.CHECKSUM_POLICY_IGNORE
import org.eclipse.aether.repository.RepositoryPolicy.UPDATE_POLICY_ALWAYS
import org.eclipse.aether.resolution.DependencyRequest
import org.marid.fx.extensions.deleteDirectoryContents
import org.marid.fx.extensions.progress
import org.marid.fx.extensions.toImmutableMap
import org.marid.fx.extensions.toTypedArray
import org.marid.fx.i18n.i18n
import org.marid.ide.child.project.Progress.*
import org.marid.ide.common.IdeProperties
import org.marid.ide.common.LocalRepositoryServer
import org.marid.ide.main.IdeServices
import org.marid.ide.project.ProjectDependencyResolver
import org.marid.ide.project.ProjectTask
import org.springframework.beans.factory.ObjectProvider
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
  private val session: ProjectSession,
  private val localRepositoryServerProvider: ObjectProvider<LocalRepositoryServer>
) : Service<Unit>() {

  private val projectInvalidationListener = InvalidationListener { session.project.dirty() }
  private val dependencyFilter = DependencyFilter { _, _ -> true }
  private var classLoader: URLClassLoader? = null

  override fun createTask(): Task<Unit> = InnerTask()

  @PostConstruct
  fun onInit() {
    services.add(this)
    session.project.observables.forEach { it.addListener(projectInvalidationListener) }
    stateProperty().addListener { _, _, s ->
      if (s == SUCCEEDED) session.project.clearDirty()
    }
    start()
  }

  @PreDestroy
  fun onDestroy() {
    services.remove(this)
    session.project.observables.forEach { it.removeListener(projectInvalidationListener) }
    classLoader?.close()
  }

  override fun toString(): String {
    return "Project build service: %s".i18n(title)
  }

  inner class InnerTask : ProjectTask<Unit>(session.project) {

    init {
      updateTitle(project.winery.name.get())
    }

    override fun callTask() {
      project.logger.info("Build started")
      invoke()
      project.logger.info("Build finished")
    }

    private fun invoke() {
      updateProgress(REPOSITORIES.progress)
      val localRepos = localRepositoryServerProvider.ifAvailable
        ?.run {
          listOf(
            RemoteRepository.Builder("m2", "default", url.toString())
              .setPolicy(RepositoryPolicy(true, UPDATE_POLICY_ALWAYS, CHECKSUM_POLICY_IGNORE))
              .build()
          )
        }
        ?: emptyList<RemoteRepository>()
      val repos = project.repositories.items
        .map {
          val builder = RemoteRepository.Builder(it.name.get(), "", it.url.get())
          if (it.url.get().startsWith("file://")) {
            builder.setPolicy(RepositoryPolicy(true, UPDATE_POLICY_ALWAYS, CHECKSUM_POLICY_IGNORE))
          }
          builder.build()
        } + localRepos

      updateProgress(DEPENDENCIES.progress)
      val dependencies = project.dependencies.items
        .map { (g, a, v) -> DefaultArtifact(g, a, "", "jar", properties.substitute(v)) }
        .map { Dependency(it, "runtime") }
      val collectRequest = CollectRequest(null as Dependency?, dependencies, repos)

      updateProgress(RESOLVE.progress)
      val result = dependencyResolver.repositorySystem.resolveDependencies(
        session.session,
        DependencyRequest(collectRequest, dependencyFilter)
      )
      if (result.collectExceptions.isNotEmpty()) {
        throw result.collectExceptions.reduce { e1, e2 -> e1.apply { addSuppressed(e2) } }
      }

      updateProgress(ARTIFACTS.progress)
      val artifacts = result.artifactResults.parallelStream()
        .map { it.artifact }
        .toImmutableMap(
          { it.groupId to it.artifactId },
          { it },
          { a, b -> maxOf(a, b, compareBy { it.version }) }
        ).values

      project.withWrite {
        updateProgress(DELETE_DIRECTORY.progress)
        project.depsDirectory.deleteDirectoryContents()

        updateProgress(COPYING.progress)
        val urls = artifacts.parallelStream()
          .map { it.file.toPath() to project.depsDirectory.resolve(it.file.name) }
          .peek { (from, to) -> Files.copy(from, to) }
          .map { (_, to) -> to.toUri().toURL() }
          .toTypedArray()

        updateProgress(CLASS_LOADER.progress)
        classLoader?.also { it.close() }
        classLoader = URLClassLoader(urls, ClassLoader.getPlatformClassLoader())

        updateProgress(SAVE.progress)
        project.save()
      }
    }
  }
}

private enum class Progress {
  REPOSITORIES,
  DEPENDENCIES,
  RESOLVE,
  ARTIFACTS,
  DELETE_DIRECTORY,
  COPYING,
  CLASS_LOADER,
  SAVE
}