package org.marid.ide.project.dialogs

import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.TextField
import org.controlsfx.validation.ValidationResult
import org.marid.fx.dialog.FxDialogData
import org.marid.fx.dialog.FxDialogProp
import org.marid.ide.project.model.FxCellar
import org.marid.ide.project.model.FxWinery

class CellarDialogData(winery: FxWinery, cellar: FxCellar?) : FxDialogData("Cellar") {

  val name = SimpleStringProperty()

  @FxDialogProp(label = "Name")
  val nameControl = TextField(cellar?.name?.get() ?: "cellar").also {
    it.textProperty().bindBidirectional(name)
  }

  init {
    validationSupport.registerValidator<String>(nameControl) { _, v ->
      when {
        v.isBlank() -> ValidationResult.fromError(nameControl, "Blank value")
        else -> ValidationResult()
      }
    }
  }
}