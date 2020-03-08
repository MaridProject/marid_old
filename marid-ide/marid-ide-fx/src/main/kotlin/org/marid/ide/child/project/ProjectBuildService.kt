package org.marid.ide.child.project

import javafx.application.Platform
import javafx.beans.InvalidationListener
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.property.ReadOnlyBooleanWrapper
import javafx.concurrent.Service
import javafx.concurrent.Task
import org.eclipse.aether.artifact.Artifact
import org.eclipse.aether.artifact.DefaultArtifact
import org.eclipse.aether.collection.CollectRequest
import org.eclipse.aether.graph.Dependency
import org.eclipse.aether.graph.DependencyFilter
import org.eclipse.aether.repository.RemoteRepository
import org.eclipse.aether.repository.RepositoryPolicy
import org.eclipse.aether.repository.RepositoryPolicy.CHECKSUM_POLICY_IGNORE
import org.eclipse.aether.repository.RepositoryPolicy.UPDATE_POLICY_ALWAYS
import org.eclipse.aether.resolution.DependencyRequest
import org.marid.fx.extensions.*
import org.marid.fx.i18n.i18n
import org.marid.ide.child.project.Progress.*
import org.marid.ide.common.IdeProperties
import org.marid.ide.common.LocalRepositoryServer
import org.marid.ide.main.IdeServices
import org.marid.ide.project.ProjectTask
import org.marid.ide.project.dependencies.DependencyResolver
import org.springframework.beans.factory.ObjectProvider
import org.springframework.stereotype.Component
import java.net.URL
import java.net.URLClassLoader
import java.nio.file.Files
import java.nio.file.Files.newDirectoryStream
import java.nio.file.Path
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Component
class ProjectBuildService(
  private val services: IdeServices,
  private val properties: IdeProperties,
  private val dependencyResolver: DependencyResolver,
  private val session: ProjectSession,
  private val localRepositoryServerProvider: ObjectProvider<LocalRepositoryServer>
) : Service<Unit>() {

  private val dirtyProperty = ReadOnlyBooleanWrapper(this, "dirty", true)
  private val projectInvalidationListener = InvalidationListener { dirtyProperty.set(true) }
  private val dependencyFilter = DependencyFilter { _, _ -> true }

  @Volatile
  private var classLoader = buildLoader(
    newDirectoryStream(session.project.runtimeDirectory, "*.jar").use { s -> s.map { it.url }.toTypedArray() },
    newDirectoryStream(session.project.depsDirectory, "*.jar").use { s -> s.map { it.url }.toTypedArray() }
  )

  override fun createTask(): Task<Unit> = InnerTask()

  @PostConstruct
  fun onInit() {
    services.add(this)
    session.project.repositories.observables.forEach { it.addListener(projectInvalidationListener) }
    session.project.dependencies.observables.forEach { it.addListener(projectInvalidationListener) }
  }

  @PreDestroy
  fun onDestroy() {
    services.remove(this)
    session.project.repositories.observables.forEach { it.removeListener(projectInvalidationListener) }
    session.project.dependencies.observables.forEach { it.removeListener(projectInvalidationListener) }
    classLoader.close()
  }

  fun <R> withClassLoader(callback: (URLClassLoader) -> R): R = callback(classLoader)

  val dirty: ReadOnlyBooleanProperty get() = dirtyProperty.readOnlyProperty

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
      Platform.runLater { dirtyProperty.set(false) }
      project.logger.info("Build finished")
    }

    private val allRepos: List<RemoteRepository>
      get() {
        val localRepos = localRepositoryServerProvider.ifAvailable?.run {
          listOf(
            RemoteRepository.Builder("m2", "default", url.toString())
              .setPolicy(RepositoryPolicy(true, UPDATE_POLICY_ALWAYS, CHECKSUM_POLICY_IGNORE))
              .build()
          )
        } ?: emptyList<RemoteRepository>()
        return project.repositories.items.map {
          val builder = RemoteRepository.Builder(it.name.get(), "", it.url.get())
          if (it.url.get().startsWith("file://")) {
            builder.setPolicy(RepositoryPolicy(true, UPDATE_POLICY_ALWAYS, CHECKSUM_POLICY_IGNORE))
          }
          builder.build()
        } + localRepos
      }

    private val allProjectDependencies
      get() = project.dependencies.items
        .map { (g, a, v) -> DefaultArtifact(g, a, "", "jar", properties.substitute(v)) }
        .map { Dependency(it, "runtime") }

    private val allRuntimeDependencies
      get() = allProjectDependencies
        .find { it.artifact.groupId == "org.marid" && it.artifact.artifactId in KNOWN_MARID_ARTIFACTS }
        ?.let { DefaultArtifact("org.marid", "marid-runtime", "", "jar", properties.substitute(it.artifact.version)) }
        ?.let { listOf(Dependency(it, "runtime")) }
        ?: emptyList()

    private fun artifacts(dependencies: List<Dependency>, repos: List<RemoteRepository>): Collection<Artifact> {
      val request = CollectRequest(null as Dependency?, dependencies, repos)
      val result = dependencyResolver.repositorySystem.resolveDependencies(
        session.session,
        DependencyRequest(request, dependencyFilter)
      )
      if (result.collectExceptions.isNotEmpty()) {
        throw result.collectExceptions.reduce { e1, e2 -> e1.apply { addSuppressed(e2) } }
      }
      return result.artifactResults.parallelStream()
        .map { it.artifact }
        .toImmutableMap(
          { it.groupId to it.artifactId },
          { it },
          { a, b -> maxOf(a, b, compareBy { it.version }) }
        ).values
    }

    private fun copyArtifacts(artifacts: Collection<Artifact>, target: Path) = artifacts.parallelStream()
      .map { it.file.toPath() to target.resolve(it.file.name) }
      .peek { (from, to) -> Files.copy(from, to) }
      .map { (_, to) -> to.toUri().toURL() }
      .toTypedArray()

    private fun invoke() = project.withWrite {
      updateProgress(DELETE_DIRECTORY.progress)
      project.depsDirectory.deleteDirectoryContents()
      project.runtimeDirectory.deleteDirectoryContents()

      updateProgress(REPOSITORIES.progress)
      val repos = allRepos

      updateProgress(DEPENDENCIES.progress)
      val artifacts = artifacts(allProjectDependencies, repos)
      val runtimeArtifacts = artifacts(allRuntimeDependencies, repos)

      updateProgress(COPYING.progress)
      val urls = copyArtifacts(artifacts, project.depsDirectory)
      val runtimeUrls = copyArtifacts(runtimeArtifacts, project.runtimeDirectory)

      updateProgress(CLASS_LOADER.progress)
      classLoader.also { it.close() }
      classLoader = buildLoader(runtimeUrls, urls)

      updateProgress(SAVE.progress)
      project.save()
    }
  }

  fun buildLoader(rt: Array<URL>, deps: Array<URL>) = URLClassLoader(rt, ClassLoader.getPlatformClassLoader())
    .let { rc ->
      object : URLClassLoader(deps, rc) {
        override fun close() {
          rc.use {
            super.close()
          }
        }
      }
    }

  companion object {
    val KNOWN_MARID_ARTIFACTS = setOf("marid-racks", "marid-db", "marid-util", "marid-proto")
  }
}

private enum class Progress {
  DELETE_DIRECTORY,
  REPOSITORIES,
  DEPENDENCIES,
  COPYING,
  CLASS_LOADER,
  SAVE
}