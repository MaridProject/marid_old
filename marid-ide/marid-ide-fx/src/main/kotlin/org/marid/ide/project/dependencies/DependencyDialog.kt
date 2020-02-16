package org.marid.ide.project.dependencies

import javafx.scene.control.*
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import javafx.stage.Stage
import javafx.util.Callback
import org.marid.fx.extensions.bindFormat
import org.marid.ide.extensions.bean
import org.marid.ide.project.model.FxDependency
import org.marid.spring.annotation.PrototypeScoped
import org.springframework.beans.factory.ObjectFactory
import org.springframework.stereotype.Component

@PrototypeScoped
@Component
class DependencyDialog(primaryStage: ObjectFactory<Stage>) : Dialog<FxDependency>() {

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

  fun init(dep: FxDependency): DependencyDialog {
    groupField.text = dep.group.get()
    artifactField.text = dep.artifact.get()
    versionField.text = dep.version.get()
    return this
  }

  private val toDependency
    get() = FxDependency(
      groupField.text.trim(),
      artifactField.text.trim(),
      versionField.text.trim()
    ).takeUnless { it.isBlank }
}