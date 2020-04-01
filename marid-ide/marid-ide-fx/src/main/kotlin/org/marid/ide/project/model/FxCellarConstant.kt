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

import javafx.beans.Observable
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import org.marid.model.CellarConstant
import org.marid.model.ConstantArgument

class FxCellarConstant : FxEntity(), CellarConstant {

  val factory = SimpleStringProperty(this, "factory", "")
  val selector = SimpleStringProperty(this, "selector", "")
  val name = SimpleStringProperty(this, "name", "")
  val arguments = FXCollections.observableArrayList(FxConstantArgument::observables)
  val observables = arrayOf<Observable>(factory, selector, name, arguments, resolvedType)

  override fun getArguments(): MutableList<out ConstantArgument> = arguments
  override fun setName(name: String) = this.name.set(name)
  override fun setFactory(factory: String) = this.factory.set(factory)
  override fun setSelector(selector: String) = this.selector.set(selector)
  override fun getName(): String = this.name.get()
  override fun getFactory(): String = this.factory.get()
  override fun getSelector(): String = this.selector.get()

  override fun addArgument(argument: ConstantArgument) {
    arguments.add(argument as FxConstantArgument)
  }
}
