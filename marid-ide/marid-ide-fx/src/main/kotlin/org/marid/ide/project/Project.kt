package org.marid.ide.project

import org.marid.fx.extensions.inf
import org.marid.fx.extensions.logger
import org.marid.fx.extensions.wrn
import org.marid.fx.i18n.localized
import org.marid.ide.project.Projects.Companion.directories
import org.marid.ide.project.Projects.Companion.writableItems
import org.marid.ide.project.xml.XmlRepositories
import org.marid.ide.project.xml.XmlWinery
import org.springframework.util.FileSystemUtils
import java.nio.file.Files

class Project(val projects: Projects, val id: String) {

  val winery = XmlWinery()
  val repositories = XmlRepositories()
  val observables = winery.observables + repositories.observables

  private val directory = directories.projectsHome.resolve(id)
  private val wineryFile = directory.resolve("winery.xml")
  private val repositoriesFile = directory.resolve("repositories.xml")
  private val resourcesDirectory = directory.resolve("resources")
  private val classesDirectory = directory.resolve("classes")
  private val depsDirectory = directory.resolve("deps")

  init {
    val existing = Files.isDirectory(directory)

    Files.createDirectories(resourcesDirectory)
    Files.createDirectories(classesDirectory)
    Files.createDirectories(depsDirectory)

    if (!existing) {
      winery.name.set("New project %d".localized(projects.items.size + 1).get())
    }
    load()
  }

  private fun load() {
    if (Files.isRegularFile(wineryFile)) winery.load(wineryFile)
    if (Files.isRegularFile(repositoriesFile)) repositories.load(repositoriesFile)
  }

  fun save() {
    winery.save(wineryFile)
    repositories.save(repositoriesFile)
  }

  fun delete() {
    writableItems -= this
    if (FileSystemUtils.deleteRecursively(directory)) {
      logger.inf("Project {0} deleted", id)
    } else {
      logger.wrn("Project {0} does not exist", id)
    }
  }

  override fun hashCode(): Int = id.hashCode()
  override fun equals(other: Any?): Boolean = (other === this) || other is Project && other.id == id
}