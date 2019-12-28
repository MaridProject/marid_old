package org.marid.ide.project.model

import javafx.beans.Observable
import javafx.beans.property.SimpleStringProperty
import org.marid.fx.property.NestedObservableProperty
import org.marid.runtime.model.Input

class InputWrapper() {

  constructor(input: Input) : this() {
    name.set(input.name)
    argument.set(ArgumentWrapperFactory.argumentWrapper(input.argument))
  }

  val name = SimpleStringProperty(this, "name", "")
  val argument = NestedObservableProperty<ArgumentWrapper>(this, "argument", ArgumentNullWrapper())

  val observables: Array<Observable> = arrayOf(name, argument)

  val input get() = Input(name.get(), argument.get().argument)
}
