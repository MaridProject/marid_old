package org.marid.ide.project.model

import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import org.marid.runtime.model.Destroyer
import org.marid.runtime.model.Initializer

sealed class MethodWrapper {

  val name = SimpleStringProperty(this, "name", "method")
  val arguments = FXCollections.observableArrayList(ArgumentWrapper::observables)

  val observables = arrayOf(name, arguments)
}

class DestroyerWrapper : MethodWrapper() {
  val destroyer
    get() = Destroyer(name.get())
      .also { it.arguments.addAll(arguments.map { a -> a.argument }) }
}

class InitializerWrapper : MethodWrapper() {
  val initializer
    get() = Initializer(name.get())
      .also { it.arguments.addAll(arguments.map { a -> a.argument }) }
}