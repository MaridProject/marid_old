package org.marid.ide.project.model

import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import org.marid.runtime.model.Rack

class RackWrapper() {

  constructor(rack: Rack) : this() {
    name.set(rack.name)
    factory.set(rack.factory)
    arguments.setAll(rack.arguments.map(ArgumentWrapperFactory::argumentWrapper))
    inputs.setAll(rack.inputs.map(::InputWrapper))
    initializers.setAll(rack.initializers.map(::InitializerWrapper))
    destroyers.setAll(rack.destroyers.map(::DestroyerWrapper))
  }

  val name = SimpleStringProperty(this, "name", "rack")
  val factory = SimpleStringProperty(this, "factory", "")

  val arguments = FXCollections.observableArrayList(ArgumentWrapper::observables)
  val inputs = FXCollections.observableArrayList(InputWrapper::observables)
  val initializers = FXCollections.observableArrayList(InitializerWrapper::observables)
  val destroyers = FXCollections.observableArrayList(DestroyerWrapper::observables)

  val observables = arrayOf(name, factory, arguments, inputs, initializers, destroyers)

  val rack
    get() = Rack(name.get(), factory.get())
      .also { it.arguments.addAll(arguments.map(ArgumentWrapper::argument)) }
      .also { it.inputs.addAll(inputs.map(InputWrapper::input)) }
      .also { it.initializers.addAll(initializers.map(InitializerWrapper::initializer)) }
      .also { it.destroyers.addAll(destroyers.map(DestroyerWrapper::destroyer)) }
}