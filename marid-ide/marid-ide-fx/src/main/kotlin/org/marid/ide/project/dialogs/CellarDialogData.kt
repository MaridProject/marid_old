package org.marid.ide.project.dialogs

import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.TextField
import org.marid.fx.dialog.FxDialogData
import org.marid.fx.dialog.FxDialogProp
import org.marid.fx.extensions.bindBoolean
import org.marid.fx.validation.validate
import org.marid.ide.project.model.FxCellar
import org.marid.ide.project.model.FxWinery

class CellarDialogData(winery: FxWinery, cellar: FxCellar?) : FxDialogData("Cellar") {

  val name = SimpleStringProperty()

  @FxDialogProp(label = "Name")
  val nameControl = TextField(cellar?.name?.get() ?: "cellar").also {
    name.bindBidirectional(it.textProperty())
    validation.add(it, name.bindBoolean { v -> v.get().isBlank() }.validate("Value is blank"))
  }
}