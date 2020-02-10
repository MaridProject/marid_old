package org.marid.ide.project

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.marid.ide.common.Directories
import org.marid.ide.common.LocalRepositoryServer
import org.springframework.stereotype.Component
import java.nio.file.Files

@Component
class Projects(
  private val directories: Directories,
  private val repositoryServer: LocalRepositoryServer
) {

  private val _items = FXCollections.observableArrayList(Project::observables)
  val items: ObservableList<Project> = FXCollections.unmodifiableObservableList(_items)

  init {
    Files.newDirectoryStream(directories.projectsHome) { Files.isDirectory(it) }.use { dirs ->
      for (dir in dirs) {
        _items += Project(this, dir.fileName.toString())
      }
    }
  }

  fun newProject(): Project {
    val project = Project(this)
    _items.add(project)
    return project
  }

  companion object {
    val Project.directories get() = projects.directories
    val Project.repositoryServer get() = projects.repositoryServer
    val Project.writableItems: ObservableList<Project> get() = projects._items
  }
}