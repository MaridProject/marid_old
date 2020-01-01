package org.marid.ide.project

import javafx.collections.FXCollections
import org.marid.fx.i18n.localized
import org.marid.ide.common.Directories
import org.marid.runtime.model.Winery
import org.springframework.stereotype.Component
import java.util.*

@Component
class Projects(internal val directories: Directories) {

  val items = FXCollections.observableArrayList(Project::observables)

  fun newProject(): Project {
    val project = Project(this, UUID.randomUUID().toString(), Winery("New project".localized.get()))
    items.add(project)
    return project
  }
}