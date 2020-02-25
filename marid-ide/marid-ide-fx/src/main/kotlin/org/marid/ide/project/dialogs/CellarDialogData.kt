package org.marid.ide.project.dialogs

import javafx.beans.property.SimpleStringProperty
import org.marid.fx.dialog.FxDialogData
import org.marid.ide.project.model.FxCellar

class CellarDialogData(cellar: FxCellar) : FxDialogData("Cellar") {

  val name = SimpleStringProperty()
}