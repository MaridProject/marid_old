package org.marid.ide.project

import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import org.marid.fx.extensions.lif
import org.marid.fx.extensions.logger
import org.marid.fx.extensions.lwn
import org.marid.fx.i18n.localized
import org.marid.ide.project.Projects.Companion.directories
import org.marid.ide.project.Projects.Companion.writableItems
import org.marid.ide.project.model.CellarWrapper
import org.marid.io.Xmls
import org.marid.runtime.model.Winery
import org.springframework.util.FileSystemUtils
import java.nio.file.Files
import javax.xml.transform.stream.StreamResult

class Project(val projects: Projects, val id: String) {

  val name = SimpleStringProperty(this, "name")
  val cellars = FXCollections.observableArrayList(CellarWrapper::observables)
  val observables = arrayOf(name, cellars)

  private val directory = directories.projectsHome.resolve(id)
  private val resourcesDirectory = directory.resolve("resources")
  private val classesDirectory = directory.resolve("classes")
  private val depsDirectory = directory.resolve("deps")

  init {
    Files.createDirectories(directory)
    Files.createDirectories(resourcesDirectory)
    Files.createDirectories(classesDirectory)
    Files.createDirectories(depsDirectory)

    if (!load()) {
      name.set("New project".localized.get())
    }
  }

  val winery
    get() = Winery(name.get())
      .also { it.cellars.addAll(cellars.map(CellarWrapper::cellar)) }

  private fun load(): Boolean {
    val file = directory.resolve("winery.xml")
    if (!Files.isRegularFile(file)) {
      return false
    }
    val winery = Xmls.read(file) { Winery(it) }
    name.set(winery.name)
    cellars.setAll(winery.cellars.map(::CellarWrapper))
    return true
  }

  fun save() {
    Xmls.writeFormatted("winery", winery::writeTo, StreamResult(directory.resolve("winery.xml").toFile()))
  }

  fun delete() {
    writableItems -= this
    if (FileSystemUtils.deleteRecursively(directory)) {
      logger.lif("Project {0} deleted", id)
    } else {
      logger.lwn("Project {0} does not exist", id)
    }
  }

  override fun hashCode(): Int {
    return id.hashCode()
  }

  override fun equals(other: Any?): Boolean {
    return when (other) {
      other === this -> true
      is Project -> other.id == id
      else -> false
    }
  }
}