/*-
 * #%L
 * marid-ide-fx
 * %%
 * Copyright (C) 2012 - 2020 MARID software development group
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

package org.marid.ide.project.model

import javafx.beans.InvalidationListener
import javafx.beans.Observable
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import org.marid.model.*

sealed class FxArgument : FxEntity(), Argument, Observable {
  abstract val observables: Array<Observable>
  val name = SimpleStringProperty(this, "name", "")
  override fun addListener(listener: InvalidationListener?) = observables.forEach { it.addListener(listener) }
  override fun removeListener(listener: InvalidationListener?) = observables.forEach { it.removeListener(listener) }
  override fun getName(): String = name.get()
  override fun setName(name: String) = this.name.set(name)
}

sealed class FxConstantArgument : FxArgument(), ConstantArgument

object FxNull : FxConstantArgument(), Null {
  override val observables = emptyArray<Observable>()
}

class FxLiteral : FxConstantArgument(), Literal {

  val type = SimpleObjectProperty<Literal.Type>(this, "type", Literal.Type.VOID)
  val value = SimpleStringProperty(this, "value", "")
  override val observables = arrayOf<Observable>(type, value, resolvedType)

  override fun setValue(value: String) = this.value.set(value)
  override fun setType(type: Literal.Type) = this.type.set(type)
  override fun getType(): Literal.Type = this.type.get()
  override fun getValue(): String = this.value.get()
}

class FxConstRef : FxConstantArgument(), ConstRef {

  val cellar = SimpleStringProperty(this, "cellar", "")
  val ref = SimpleStringProperty(this, "ref", "")
  override val observables = arrayOf<Observable>(cellar, ref, resolvedType)

  override fun setRef(ref: String) = this.ref.set(ref)
  override fun getCellar(): String = this.cellar.get()
  override fun getRef(): String = this.ref.get()
  override fun setCellar(cellar: String) = this.cellar.set(cellar)
}

class FxRef : FxArgument(), Ref {

  val cellar = SimpleStringProperty(this, "cellar", "")
  val rack = SimpleStringProperty(this, "rack", "")
  val ref = SimpleStringProperty(this, "ref", "")
  override val observables = arrayOf<Observable>(cellar, rack, ref, resolvedType)

  override fun setRef(ref: String) = this.ref.set(ref)
  override fun getRack(): String = this.rack.get()
  override fun getCellar(): String = this.cellar.get()
  override fun getRef(): String = this.ref.get()
  override fun setRack(rack: String) = this.rack.set(rack)
  override fun setCellar(cellar: String) = this.cellar.set(cellar)
}
