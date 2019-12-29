package org.marid.ide.project

import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import org.marid.fx.i18n.localized
import org.marid.ide.common.Directories
import org.marid.ide.project.model.CellarWrapper
import org.marid.runtime.model.Winery
import org.springframework.stereotype.Component
import java.nio.file.Files
import java.util.*

@Component
class Projects(private val directories: Directories) {

  val items = FXCollections.observableArrayList(Project::observables)

  fun newProject(): Project {
    val project = Project()
    items.add(project)
    return project
  }

  fun save(index: Int) {
    val project = items[index]
    val winery = project.winery
  }

  inner class Project {

    val id = UUID.randomUUID().toString()
    val name = SimpleStringProperty(this, "name", "New project".localized.get())
    val cellars = FXCollections.observableArrayList(CellarWrapper::observables)

    val observables = arrayOf(name, cellars)

    private val directory = this@Projects.directories.projectsHome.resolve(id)

    init {
      Files.createDirectories(directory)
    }

    val winery
      get() = Winery(name.get())
        .also { it.cellars.addAll(cellars.map(CellarWrapper::cellar)) }

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
}