package org.marid.ide.main

import javafx.scene.control.TabPane
import org.marid.ide.project.ProjectsTab
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class IdeTabs : TabPane() {
}

@Component
class IdeTabsCollector(private val ideTabs: IdeTabs) {

  @Autowired
  fun initTabs(projectsTab: ProjectsTab) {
    ideTabs.tabs += listOf(projectsTab)
  }
}