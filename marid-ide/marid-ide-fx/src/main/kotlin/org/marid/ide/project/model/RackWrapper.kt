package org.marid.ide.project.model

import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import org.marid.runtime.model.Rack

class RackWrapper {

  val name = SimpleStringProperty(this, "name", "rack")
  val factory = SimpleStringProperty(this, "factory", "")

  val arguments = FXCollections.observableArrayList(ArgumentWrapper::observables)
  val inputs = FXCollections.observableArrayList(InputWrapper::observables)
  val initializers = FXCollections.observableArrayList(InitializerWrapper::observables)
  val destroyers = FXCollections.observableArrayList(DestroyerWrapper::observables)

  val observables = arrayOf(name, factory, arguments, inputs, initializers, destroyers)

  val rack
    get() = Rack(name.get(), factory.get())
      .also { it.arguments.addAll(arguments.map { a -> a.argument }) }
      .also { it.inputs.addAll(inputs.map { i -> i.input }) }
      .also { it.initializers.addAll(initializers.map { i -> i.initializer }) }
      .also { it.destroyers.addAll(destroyers.map { d -> d.destroyer }) }
}