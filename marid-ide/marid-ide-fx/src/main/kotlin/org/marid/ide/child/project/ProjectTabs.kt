package org.marid.ide.child.project

import javafx.geometry.Side
import javafx.scene.control.TabPane
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ProjectTabs : TabPane() {
  init {
    side = Side.BOTTOM
  }
}

@Component
class ProjectsTabsCollector(private val tabs: ProjectTabs) {

  @Autowired
  private fun init(
    wineryTab: WineryTab,
    dependenciesTab: DependenciesTab
  ) {
    tabs.tabs += listOf(
      wineryTab,
      dependenciesTab
    )
  }
}