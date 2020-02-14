package org.marid.ide.project.model

import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import org.marid.runtime.model.CellarConstantImpl

class CellarConstantWrapper() {

  constructor(cellarConstant: CellarConstantImpl) : this() {
    name.set(cellarConstant.name)
    factory.set(cellarConstant.factory)
    selector.set(cellarConstant.selector)
    arguments.setAll(cellarConstant.arguments.map(ArgumentWrapperFactory::constantArgumentWrapper))
  }

  val name = SimpleStringProperty(this, "name", "constant")
  val factory = SimpleStringProperty(this, "factory", "factory")
  val selector = SimpleStringProperty(this, "method", "method")
  val arguments = FXCollections.observableArrayList(ConstantArgumentWrapper::observables)

  val observables = arrayOf(name, factory, selector, arguments)

  val constant
    get() = CellarConstantImpl(factory.get(), selector.get(), name.get())
      .also { it.arguments.addAll(arguments.map(ConstantArgumentWrapper::argument)) }
}