package org.marid.ide.child.project

import javafx.scene.control.Dialog
import org.marid.fx.action.Fx
import org.marid.ide.extensions.bean
import org.marid.ide.project.Project
import org.marid.ide.project.xml.XmlDependency
import org.marid.spring.annotation.PrototypeScoped
import org.springframework.beans.factory.ObjectFactory
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class DependenciesActions(projectFactory: ObjectFactory<Project>) {

  private val project = projectFactory.bean

  @Bean fun loadDefaultDependencies() = Fx(
    text = "Add standard libraries",
    icon = "icons/standard.png",
    handler = { project.dependencies.loadDefault() }
  )

  @Bean fun addDependency(dialog: ObjectFactory<AddDependencyDialog>) = Fx(
    text = "Add dependency",
    icon = "icons/add.png",
    handler = { dialog.bean.showAndWait().ifPresent { project.dependencies.items += it } }
  )
}

@PrototypeScoped
@Component
class AddDependencyDialog : Dialog<XmlDependency>() {

}