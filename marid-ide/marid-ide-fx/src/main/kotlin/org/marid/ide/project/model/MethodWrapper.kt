package org.marid.ide.project.model

import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import org.marid.runtime.model.Destroyer
import org.marid.runtime.model.Initializer
import org.marid.runtime.model.Method

sealed class MethodWrapper() {

  constructor(method: Method<*>) : this() {
    name.set(method.getName())
    arguments.setAll(method.getArguments().map(ArgumentWrapperFactory::argumentWrapper))
  }

  val name = SimpleStringProperty(this, "name", "method")
  val arguments = FXCollections.observableArrayList(ArgumentWrapper::observables)

  val observables = arrayOf(name, arguments)
}

class DestroyerWrapper : MethodWrapper {

  constructor() : super()
  constructor(destroyer: Destroyer) : super(destroyer)

  val destroyer
    get() = Destroyer(name.get())
      .also { it.arguments.addAll(arguments.map(ArgumentWrapper::argument)) }
}

class InitializerWrapper : MethodWrapper {

  constructor(): super()
  constructor(initializer: Initializer): super(initializer)

  val initializer
    get() = Initializer(name.get())
      .also { it.arguments.addAll(arguments.map(ArgumentWrapper::argument)) }
}