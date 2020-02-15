package org.marid.ide.project.model

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import org.marid.runtime.model.Output
import java.lang.reflect.Type

class FxOutput : FxEntity(), Output, ResolvedTyped {

  val name = SimpleStringProperty(this, "name")
  override val resolvedType = SimpleObjectProperty<Type>(this, "resolvedType")

  override fun setName(name: String) = this.name.set(name)
  override fun getName(): String = this.name.get()
}