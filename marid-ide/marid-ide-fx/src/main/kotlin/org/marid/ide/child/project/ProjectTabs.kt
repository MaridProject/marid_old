package org.marid.ide.child.project

import javafx.scene.control.TabPane
import org.marid.ide.child.project.dependencies.DependenciesTab
import org.marid.ide.child.project.repositories.RepositoriesTab
import org.marid.ide.child.project.winery.WineryTab
import org.marid.ide.common.IdePreferences
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ProjectTabs(idePreferences: IdePreferences) : TabPane() {

  init {
    sideProperty().bind(idePreferences.alternateTabsSide)
  }
}

@Component
class ProjectsTabsCollector(private val tabs: ProjectTabs) {

  @Autowired
  private fun init(
    wineryTab: WineryTab,
    repositoriesTab: RepositoriesTab,
    dependenciesTab: DependenciesTab
  ) {
    tabs.tabs += listOf(
      wineryTab,
      repositoriesTab,
      dependenciesTab
    )
  }
}