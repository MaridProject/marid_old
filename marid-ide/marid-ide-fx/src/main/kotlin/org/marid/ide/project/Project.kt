package org.marid.ide.project

import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.property.ReadOnlyBooleanWrapper
import javafx.beans.property.ReadOnlyDoubleProperty
import javafx.beans.property.ReadOnlyDoubleWrapper
import org.marid.fx.extensions.INFO
import org.marid.fx.extensions.WARN
import org.marid.fx.extensions.runFx
import org.marid.fx.i18n.localized
import org.marid.ide.project.Projects.Companion.directories
import org.marid.ide.project.Projects.Companion.writableItems
import org.marid.ide.project.xml.XmlDependencies
import org.marid.ide.project.xml.XmlRepositories
import org.marid.ide.project.xml.XmlRepository
import org.marid.ide.project.xml.XmlWinery
import org.springframework.util.FileSystemUtils
import java.nio.file.Files
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

  val directory = directories.projectsHome.resolve(id)
  val wineryFile = directory.resolve("winery.xml")
  val repositoriesFile = directory.resolve("repositories.xml")
  val dependenciesFile = directory.resolve("dependencies.xml")
  val resourcesDirectory = directory.resolve("resources")
  val classesDirectory = directory.resolve("classes")
  val depsDirectory = directory.resolve("deps")
  val logger = Logger.getLogger(id)

  private val lockedProperty = ReadOnlyBooleanWrapper(this, "locked")
  private val progressProperty = ReadOnlyDoubleWrapper(this, "progress", 0.0)
  private val lock = ReentrantReadWriteLock()

  init {
    val existing = Files.isDirectory(directory)

    if (!existing) {
      winery.name.set("New project %d".localized(projects.items.size + 1).get())
    }

    Files.createDirectories(resourcesDirectory)
    Files.createDirectories(classesDirectory)
    Files.createDirectories(depsDirectory)

    load()

    if (repositories.items.isEmpty()) {
      repositories.items += XmlRepository("default", "https://repo1.maven.org/maven2/")
    }

    if (!existing) {
      save()
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
      logger.INFO("Project {0} deleted", id)
    } else {
      logger.WARN("Project {0} does not exist", id)
    }
  }

  fun <R> withRead(callback: (Project) -> R): R = lock.read { callback(this) }
  fun <R> withWrite(callback: (Project) -> R): R {
    runFx { lockedProperty.set(true) }
    try {
      return lock.write { callback(this) }
    } finally {
      runFx { lockedProperty.set(false) }
    }
  }

  fun withProgress(code: (setter: (progress: Double) -> Unit) -> Unit) {
    try {
      code { progress -> runFx { progressProperty.set(progress) } }
    } finally {
      runFx { progressProperty.set(0.0) }
    }
  }

  val progress: ReadOnlyDoubleProperty = progressProperty.readOnlyProperty
  val locked: ReadOnlyBooleanProperty = lockedProperty.readOnlyProperty

  override fun hashCode(): Int = id.hashCode()
  override fun equals(other: Any?): Boolean = (other === this) || other is Project && other.id == id
}