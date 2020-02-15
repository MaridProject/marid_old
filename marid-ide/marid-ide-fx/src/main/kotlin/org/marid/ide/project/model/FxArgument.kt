package org.marid.ide.project.model

import javafx.beans.InvalidationListener
import javafx.beans.Observable
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import org.marid.runtime.model.*
import java.lang.reflect.Type

abstract class FxArgument : FxEntity(), Argument, Observable, ResolvedTyped {
  override val resolvedType = SimpleObjectProperty<Type>(this, "resolvedType")
  abstract val observables: Array<Observable>
  override fun addListener(listener: InvalidationListener?) = observables.forEach { it.addListener(listener) }
  override fun removeListener(listener: InvalidationListener?) = observables.forEach { it.removeListener(listener) }
}

abstract class FxConstantArgument : FxArgument(), ConstantArgument

object FxNull : FxConstantArgument(), Null {
  override val observables = emptyArray<Observable>()
}

class FxLiteral : FxConstantArgument(), Literal {

  val type = SimpleObjectProperty<Literal.Type>(this, "type")
  val value = SimpleStringProperty(this, "value")
  override val observables = arrayOf<Observable>(type, value)

  override fun setValue(value: String) = this.value.set(value)
  override fun setType(type: Literal.Type) = this.type.set(type)
  override fun getType(): Literal.Type = this.type.get()
  override fun getValue(): String = this.value.get()
}

class FxConstRef : FxConstantArgument(), ConstRef {

  val cellar = SimpleStringProperty(this, "cellar")
  val ref = SimpleStringProperty(this, "ref")
  override val observables = arrayOf<Observable>(cellar, ref)

  override fun setRef(ref: String) = this.ref.set(ref)
  override fun getCellar(): String = this.cellar.get()
  override fun getRef(): String = this.ref.get()
  override fun setCellar(cellar: String) = this.cellar.set(cellar)
}

class FxRef : FxArgument(), Ref {

  val cellar = SimpleStringProperty(this, "cellar")
  val rack = SimpleStringProperty(this, "rack")
  val ref = SimpleStringProperty(this, "ref")
  override val observables = arrayOf<Observable>(cellar, rack, ref)

  override fun setRef(ref: String) = this.ref.set(ref)
  override fun getRack(): String = this.rack.get()
  override fun getCellar(): String = this.cellar.get()
  override fun getRef(): String = this.ref.get()
  override fun setRack(rack: String) = this.rack.set(rack)
  override fun setCellar(cellar: String) = this.cellar.set(cellar)
}