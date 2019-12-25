package org.marid.ide.project

import javafx.collections.FXCollections
import org.marid.ide.common.Directories
import org.springframework.stereotype.Component

@Component
class Projects(directories: Directories) {

  val items = FXCollections.observableArrayList<Project> { it.observables }

  fun save(project: Project) {

  }
}