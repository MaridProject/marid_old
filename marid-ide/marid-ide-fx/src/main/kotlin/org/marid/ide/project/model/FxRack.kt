package org.marid.ide.project.model

import javafx.beans.Observable
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import org.marid.model.Argument
import org.marid.model.Initializer
import org.marid.model.Rack

class FxRack : FxEntity(), Rack {

  val factory = SimpleStringProperty(this, "factory", "")
  val name = SimpleStringProperty(this, "name", "")
  val arguments = FXCollections.observableArrayList(FxArgument::observables)
  val outputs = FXCollections.observableArrayList(FxOutput::observables)
  val initializers = FXCollections.observableArrayList(FxInitializer::observables)
  val varargType = SimpleStringProperty(this, "varargType", "")
  val observables = arrayOf<Observable>(factory, name, arguments, outputs, initializers, varargType, resolvedType)

  override fun getArguments(): MutableList<out Argument> = arguments
  override fun getName(): String = this.name.get()
  override fun getInitializers(): MutableList<out Initializer> = initializers
  override fun setName(name: String) = this.name.set(name)
  override fun setFactory(factory: String) = this.factory.set(factory)
  override fun getFactory(): String = this.factory.get()
  override fun getVarargType(): String = this.varargType.get()
  override fun setVarargType(type: String) = this.varargType.set(type)

  override fun addArgument(argument: Argument) {
    arguments.add(argument as FxArgument)
  }

  override fun addInitializer(initializer: Initializer) {
    initializers.add(initializer as FxInitializer)
  }
}