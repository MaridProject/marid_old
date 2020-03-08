package org.marid.ide.child.project

import javafx.scene.control.Tab
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import org.marid.fx.extensions.bindObject
import org.marid.ide.extensions.bean
import org.marid.ide.main.IdeTabs
import org.marid.ide.project.Project
import org.springframework.beans.factory.ObjectFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.support.GenericApplicationContext
import org.springframework.stereotype.Component

@Component
@ComponentScan
class ProjectTab(
  pane: ProjectChildPane,
  project: ObjectFactory<Project>,
  buildService: ProjectBuildService
) : Tab(null, pane) {

  private val project = project.bean

  init {
    id = this.project.id
    textProperty().bind(this.project.winery.name)
    graphicProperty().bind(buildService.dirty.bindObject {
      val icon = if (it.get()) "icons/modified-project.png" else "icons/unmodified-project.png"
      ImageView(Image(icon, 18.0, 18.0, true, true))
    })
    isClosable = true
  }

  @Autowired
  private fun initContext(context: GenericApplicationContext) {
    tabPaneProperty().addListener { _, _, n -> n ?: context.close() }
  }

  @Autowired
  private fun initTabs(tabs: IdeTabs) {
    tabs.tabs += this
    tabs.selectionModel.select(this)
  }
}