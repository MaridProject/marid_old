package org.marid.ide.project

import javafx.collections.FXCollections
import org.marid.ide.common.Directories
import org.springframework.stereotype.Component

@Component
class Projects(private val directories: Directories) {

  val items = FXCollections.observableArrayList(Project::observables)

  fun save(project: Project) {
    if (!items.contains(project))
      items.add(project)

    val winery = project.winery


  }
}