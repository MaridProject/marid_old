package org.marid.ide.project.model

import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.marid.runtime.model.*

sealed class MethodWrapper<A : ArgumentWrapper, M : Method<M, MA>, MA : Argument>() {

  constructor(method: Method<M, MA>, factory: (MA) -> A) : this() {
    name.set(method.name)
    arguments.setAll(method.arguments.map(factory))
  }

  val name = SimpleStringProperty(this, "name", "method")
  val arguments: ObservableList<A> = FXCollections.observableArrayList<A>(ArgumentWrapper::observables)

  val observables = arrayOf(name, arguments)
}

class DestroyerWrapper : MethodWrapper<ConstantArgumentWrapper, Destroyer, ConstantArgument> {

  constructor() : super()
  constructor(destroyer: Destroyer) : super(destroyer, ArgumentWrapperFactory::constantArgumentWrapper)

  val destroyer
    get() = Destroyer(name.get())
      .also { it.arguments.addAll(arguments.map(ConstantArgumentWrapper::argument)) }
}

class InitializerWrapper : MethodWrapper<ArgumentWrapper, Initializer, Argument> {

  constructor() : super()
  constructor(initializer: Initializer) : super(initializer, ArgumentWrapperFactory::argumentWrapper)

  val initializer
    get() = Initializer(name.get())
      .also { it.arguments.addAll(arguments.map(ArgumentWrapper::argument)) }
}