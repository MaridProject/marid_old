package org.marid.ide.child.project

import javafx.scene.control.*
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import javafx.stage.Stage
import javafx.util.Callback
import org.marid.fx.action.Fx
import org.marid.fx.extensions.bindFormat
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

  @Bean fun addDependency(dialog: ObjectFactory<DependencyDialog>) = Fx(
    text = "Add dependency",
    icon = "icons/add.png",
    handler = { dialog.bean.showAndWait().ifPresent { project.dependencies.items += it } }
  )

  @Bean fun sortDependencies() = Fx(
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
}

@PrototypeScoped
@Component
class DependencyDialog(primaryStage: ObjectFactory<Stage>) : Dialog<XmlDependency>() {

  private val groupField = TextField("")
  private val artifactField = TextField("")
  private val versionField = TextField("")

  init {
    initOwner(primaryStage.bean)
    isResizable = true
    titleProperty().bind(
      "Dependency: %s:%s:%s".bindFormat(
        groupField.textProperty(),
        artifactField.textProperty(),
        versionField.textProperty()
      )
    )
    dialogPane.setPrefSize(600.0, 400.0)
    dialogPane.buttonTypes += listOf(ButtonType.APPLY, ButtonType.CLOSE)
    dialogPane.content = GridPane().apply {
      hgap = 10.0
      vgap = 10.0
      columnConstraints += ColumnConstraints()
      columnConstraints += ColumnConstraints().apply {
        isFillWidth = true
        hgrow = Priority.ALWAYS
      }
      addRow(0, Label("group"), groupField)
      addRow(1, Label("artifact"), artifactField)
      addRow(2, Label("version"), versionField)
    }
    resultConverter = Callback {
      when (it.buttonData) {
        ButtonBar.ButtonData.APPLY -> toDependency
        else -> null
      }
    }
  }

  fun init(dep: XmlDependency): DependencyDialog {
    groupField.text = dep.group.get()
    artifactField.text = dep.artifact.get()
    versionField.text = dep.version.get()
    return this
  }

  private val toDependency
    get() = XmlDependency(
      groupField.text.trim(),
      artifactField.text.trim(),
      versionField.text.trim()
    ).takeUnless { it.isBlank }
}