package org.marid.ide.main

import javafx.scene.control.TabPane
import org.marid.ide.common.IdePreferences
import org.marid.ide.project.ProjectsTab
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class IdeTabs(idePreferences: IdePreferences) : TabPane() {
  init {
    sideProperty().bind(idePreferences.primaryTabsSide)
  }
}

@Component
class IdeTabsCollector(private val ideTabs: IdeTabs) {

  @Autowired
  fun initTabs(projectsTab: ProjectsTab) {
    ideTabs.tabs += listOf(projectsTab)
  }
}