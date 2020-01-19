package org.marid.ide.child.project

import javafx.scene.control.*
import javafx.scene.input.ContextMenuEvent
import javafx.scene.layout.BorderPane
import javafx.util.Callback
import org.marid.fx.action.Fx
import org.marid.fx.action.configure
import org.marid.fx.action.menuItem
import org.marid.fx.action.toolButton
import org.marid.fx.extensions.column
import org.marid.fx.extensions.readOnlyProp
import org.marid.ide.extensions.bean
import org.marid.ide.project.Project
import org.marid.ide.project.xml.XmlDependency
import org.springframework.beans.factory.ObjectFactory
import org.springframework.stereotype.Component

@Component
class DependenciesTab(contents: DependenciesTabContents) : Tab(null, contents) {
  init {
    isClosable = false
    configure(Fx(text = "Dependencies", icon = "icons/dependency.png"))
  }
}

@Component
class DependenciesTabContents(
  projectFactory: ObjectFactory<Project>,
  private val dependencyDialogFactory: ObjectFactory<DependencyDialog>
) : BorderPane() {

  private val project = projectFactory.bean

  private fun editFx(dep: XmlDependency) = Fx(
    text = "Edit...",
    icon = "icons/edit.png",
    handler = { dependencyDialogFactory.bean.init(dep).showAndWait().ifPresent(dep::copyFrom) }
  )

  private val loadDefaultDependencies = Fx(
    text = "Add standard libraries",
    icon = "icons/standard.png",
    handler = { project.dependencies.loadDefault() }
  )

  private val addDependency = Fx(
    text = "Add dependency",
    icon = "icons/add.png",
    handler = { dependencyDialogFactory.bean.showAndWait().ifPresent { project.dependencies.items += it } }
  )

  private val sortDependencies = Fx(
    text = "Sort dependencies",
    icon = "icons/sort.png",
    handler = {
      project.dependencies.items.sortWith(
        compareBy(
          { it.group.get() },
          { it.artifact.get() },
          { it.version.get() }
        )
      )
    }
  )

  private val toolbar = ToolBar(
    loadDefaultDependencies.toolButton,
    Separator(),
    addDependency.toolButton
  )

  private val list = TableView(project.dependencies.items).apply {
    rowFactory = Callback {
      TableRow<XmlDependency>().apply {
        val menu = ContextMenu().also { contextMenu = it }
        addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED) {
          menu.items.clear()
          menu.items += listOf(
            addDependency.menuItem,
            SeparatorMenuItem(),
            sortDependencies.menuItem
          )
          item?.also { curItem ->
            menu.items += listOf(
              SeparatorMenuItem(),
              editFx(curItem).menuItem
            )
          }
        }
      }
    }
    column(350, "group".readOnlyProp) { it.group }
    column(300, "artifact".readOnlyProp) { it.artifact }
    column(200, "version".readOnlyProp) { it.version }
  }

  init {
    top = toolbar
    center = list
  }
}