package org.marid.ide.project.model

import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.marid.runtime.model.*

sealed class MethodWrapper<A : ArgumentWrapper, M : AbstractMethod<M, MA>, MA : ArgumentImpl>() {

  constructor(method: AbstractMethod<M, MA>, factory: (MA) -> A) : this() {
    name.set(method.name)
    arguments.setAll(method.arguments.map(factory))
  }

  val name = SimpleStringProperty(this, "name", "method")
  val arguments: ObservableList<A> = FXCollections.observableArrayList<A>(ArgumentWrapper::observables)

  val observables = arrayOf(name, arguments)
}

class DestroyerWrapper : MethodWrapper<ConstantArgumentWrapper, DestroyerImpl, AbstractConstant> {

  constructor() : super()
  constructor(destroyer: DestroyerImpl) : super(destroyer, ArgumentWrapperFactory::constantArgumentWrapper)

  val destroyer
    get() = DestroyerImpl(name.get())
      .also { it.arguments.addAll(arguments.map(ConstantArgumentWrapper::argument)) }
}

class InitializerWrapper : MethodWrapper<ArgumentWrapper, InitializerImpl, ArgumentImpl> {

  constructor() : super()
  constructor(initializer: InitializerImpl) : super(initializer, ArgumentWrapperFactory::argumentWrapper)

  val initializer
    get() = InitializerImpl(name.get())
      .also { it.arguments.addAll(arguments.map(ArgumentWrapper::argument)) }
}