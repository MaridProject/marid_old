package org.marid.ide.project.model

import javafx.beans.Observable
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import org.marid.runtime.model.Argument
import org.marid.runtime.model.Initializer

class FxInitializer : FxEntity(), Initializer {

  val name = SimpleStringProperty(this, "name", "")
  val varargType = SimpleStringProperty(this, "varargType", "")
  val arguments = FXCollections.observableArrayList(FxArgument::observables)
  val observables = arrayOf<Observable>(name, varargType, arguments, resolvedType)

  override fun getArguments(): MutableList<out Argument> = arguments
  override fun setName(name: String) = this.name.set(name)
  override fun getName(): String = this.name.get()
  override fun getVarargType(): String = this.varargType.get()
  override fun setVarargType(type: String) = this.varargType.set(type)

  override fun addArgument(argument: Argument) {
    arguments.add(argument as FxArgument)
  }
}