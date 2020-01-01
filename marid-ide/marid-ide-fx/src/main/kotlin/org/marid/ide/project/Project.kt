package org.marid.ide.project

import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import org.marid.ide.project.model.CellarWrapper
import org.marid.io.Xmls
import org.marid.runtime.model.Winery
import java.nio.file.Files
import javax.xml.transform.stream.StreamResult

class Project(projects: Projects, val id: String, winery: Winery) {

  val name = SimpleStringProperty(this, "name", winery.name)
  val cellars = FXCollections.observableArrayList(CellarWrapper::observables)

  val observables = arrayOf(name, cellars)

  private val directory = projects.directories.projectsHome.resolve(id)
  private val resourcesDirectory = directory.resolve("resources")
  private val classesDirectory = directory.resolve("classes")
  private val depsDirectory = directory.resolve("deps")

  init {
    Files.createDirectories(directory)
    Files.createDirectories(resourcesDirectory)
    Files.createDirectories(classesDirectory)
    Files.createDirectories(depsDirectory)

    cellars.setAll(winery.cellars.map(::CellarWrapper))
  }

  val winery
    get() = Winery(name.get())
      .also { it.cellars.addAll(cellars.map(CellarWrapper::cellar)) }

  fun save() {
    Xmls.writeFormatted("winery", winery::writeTo, StreamResult(directory.resolve("winery.xml").toFile()))
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