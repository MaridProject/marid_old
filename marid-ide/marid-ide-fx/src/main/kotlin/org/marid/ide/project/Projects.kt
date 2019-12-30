package org.marid.ide.project

import javafx.collections.FXCollections
import org.marid.ide.common.Directories
import org.springframework.stereotype.Component

@Component
class Projects(internal val directories: Directories) {

  val items = FXCollections.observableArrayList(Project::observables)

  fun newProject(): Project {
    val project = Project(this)
    items.add(project)
    return project
  }
}