package org.marid.ide.project

import javafx.beans.InvalidationListener
import org.apache.ivy.Ivy
import org.apache.ivy.core.module.descriptor.DefaultModuleDescriptor
import org.apache.ivy.core.module.id.ModuleRevisionId
import org.apache.ivy.core.resolve.ResolveOptions
import org.apache.ivy.core.settings.IvySettings
import org.apache.ivy.plugins.resolver.ChainResolver
import org.apache.ivy.plugins.resolver.FileSystemResolver
import org.apache.ivy.plugins.resolver.IBiblioResolver
import org.marid.fx.extensions.inf
import org.marid.fx.extensions.logger
import org.marid.fx.extensions.wrn
import org.marid.fx.i18n.localized
import org.marid.ide.log.IdeMessageLogger
import org.marid.ide.project.Projects.Companion.directories
import org.marid.ide.project.Projects.Companion.writableItems
import org.marid.ide.project.xml.XmlDependencies
import org.marid.ide.project.xml.XmlRepositories
import org.marid.ide.project.xml.XmlRepository
import org.marid.ide.project.xml.XmlWinery
import org.springframework.util.FileSystemUtils
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.locks.ReentrantReadWriteLock
import java.util.logging.Logger
import kotlin.concurrent.read
import kotlin.concurrent.write

class Project(val projects: Projects, val id: String) {

  constructor(projects: Projects) : this(projects, System.currentTimeMillis().toString(Character.MAX_RADIX))

  val winery = XmlWinery()
  val repositories = XmlRepositories()
  val dependencies = XmlDependencies()
  val observables = winery.observables + repositories.observables + dependencies.observables
  val resolveOptions = ResolveOptions()
  val moduleDescriptor
    get() = DefaultModuleDescriptor.newDefaultInstance(
      ModuleRevisionId.newInstance(
        winery.group.get(),
        winery.name.get(),
        winery.version.get()
      )
    )

  val directory = directories.projectsHome.resolve(id)
  val wineryFile = directory.resolve("winery.xml")
  val repositoriesFile = directory.resolve("repositories.xml")
  val dependenciesFile = directory.resolve("dependencies.xml")
  val resourcesDirectory = directory.resolve("resources")
  val classesDirectory = directory.resolve("classes")
  val depsDirectory = directory.resolve("deps")
  val ivyDirectory = directory.resolve("ivy")
  val ivyCacheDirectory = ivyDirectory.resolve("cache")

  private val lock = ReentrantReadWriteLock()

  init {
    val existing = Files.isDirectory(directory)

    if (!existing) {
      winery.name.set("New project %d".localized(projects.items.size + 1).get())
    }

    Files.createDirectories(resourcesDirectory)
    Files.createDirectories(classesDirectory)
    Files.createDirectories(depsDirectory)
    Files.createDirectories(ivyCacheDirectory)

    load()

    if (repositories.items.isEmpty()) {
      repositories.items += XmlRepository("default", "http://repo2.maven.org/maven2/")
    }

    if (!existing) {
      save()
    }
  }

  val ivyMessageLogger = IdeMessageLogger(Logger.getLogger(id))

  private val dependencyResolver = ChainResolver()
    .apply { name = "default" }

  private val ivySettings = IvySettings().apply {
    baseDir = ivyDirectory.toFile()
    defaultCache = ivyCacheDirectory.toFile()
    defaultIvyUserDir = ivyDirectory.toFile()
    addResolver(dependencyResolver)
    setDefaultResolver("default")
  }

  private val ivy: Ivy = Ivy.newInstance(ivySettings).apply {
    loggerEngine.setDefaultLogger(ivyMessageLogger)
  }

  init {
    refreshRepos()
    repositories.items.addListener(InvalidationListener { refreshRepos() })
  }

  private fun refreshRepos() {
    dependencyResolver.resolvers.clear()
    val m2 = Path.of(System.getProperty("user.home")).resolve(".m2").resolve("repository")
    if (Files.isDirectory(m2)) {
      dependencyResolver.add(FileSystemResolver().apply {
        isM2compatible = true
        val pattern = m2
          .resolve("[organisation]")
          .resolve("[module]")
          .resolve("[revision]")
          .resolve("[module]-[revision](-[classifier]).[ext]")
          .toString()
        addArtifactPattern(pattern)
        addIvyPattern(pattern)
      })
    }
    repositories.items.forEach { repo ->
      dependencyResolver.add(IBiblioResolver().apply {
        isM2compatible = true
        isUsepoms = true
        name = repo.name.get()
        root = repo.url.get()
      })
    }
  }

  private fun load() {
    if (Files.isRegularFile(wineryFile)) winery.load(wineryFile)
    if (Files.isRegularFile(repositoriesFile)) repositories.load(repositoriesFile)
    if (Files.isRegularFile(dependenciesFile)) dependencies.load(dependenciesFile)

    if (dependencies.items.isEmpty()) {
      dependencies.loadDefault()
    }
  }

  fun save() {
    winery.save(wineryFile)
    repositories.save(repositoriesFile)
    dependencies.save(dependenciesFile)
  }

  fun delete() {
    writableItems -= this
    if (FileSystemUtils.deleteRecursively(directory)) {
      logger.inf("Project {0} deleted", id)
    } else {
      logger.wrn("Project {0} does not exist", id)
    }
  }

  fun clean() {
    withIvy { ivy.resolutionCacheManager.clean() }
  }

  fun <R> withIvy(callback: Project.(Ivy) -> R): R = lock.write {
    ivy.pushContext()
    try {
      callback(this, ivy)
    } finally {
      ivy.popContext()
    }
  }

  operator fun <R> invoke(callback: Project.() -> R): R = lock.read { callback(this) }

  override fun hashCode(): Int = id.hashCode()
  override fun equals(other: Any?): Boolean = (other === this) || other is Project && other.id == id
}