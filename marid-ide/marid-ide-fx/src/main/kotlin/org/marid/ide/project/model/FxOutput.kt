package org.marid.ide.project.model

import javafx.beans.Observable
import javafx.beans.property.SimpleStringProperty
import org.marid.model.Output

class FxOutput : FxEntity(), Output {

  val name = SimpleStringProperty(this, "name", "")
  val observables = arrayOf<Observable>(name, resolvedType)

  override fun setName(name: String) = this.name.set(name)
  override fun getName(): String = this.name.get()
}