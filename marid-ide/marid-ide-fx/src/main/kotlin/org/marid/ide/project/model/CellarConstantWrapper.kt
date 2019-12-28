package org.marid.ide.project.model

import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import org.marid.runtime.model.CellarConstant

class CellarConstantWrapper {

  val name = SimpleStringProperty(this, "name", "constant")
  val factory = SimpleStringProperty(this, "factory", "factory")
  val selector = SimpleStringProperty(this, "method", "method")
  val arguments = FXCollections.observableArrayList(ConstantArgumentWrapper::observables)

  val observables = arrayOf(name, factory, selector, arguments)

  val constant
    get() = CellarConstant(factory.get(), selector.get(), name.get())
      .also { it.arguments.addAll(arguments.map { a -> a.argument }) }
}