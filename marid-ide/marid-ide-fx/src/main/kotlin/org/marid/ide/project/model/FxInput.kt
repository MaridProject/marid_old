package org.marid.ide.project.model

import javafx.beans.Observable
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import org.marid.fx.property.NestedObservableProperty
import org.marid.runtime.model.Argument
import org.marid.runtime.model.Input
import java.lang.reflect.Type

class FxInput : FxEntity(), Input, ResolvedTyped {

  val name = SimpleStringProperty(this, "name")
  val argument = NestedObservableProperty<FxArgument>(this, "argument", FxNull)
  override val resolvedType = SimpleObjectProperty<Type>(this, "resolvedType")
  val observables = arrayOf<Observable>(name, argument, resolvedType)

  override fun setName(name: String) = this.name.set(name)
  override fun getArgument(): Argument = this.argument.get()
  override fun getName(): String = this.name.get()
  override fun setArgument(argument: Argument) = this.argument.set(argument as FxArgument)
}