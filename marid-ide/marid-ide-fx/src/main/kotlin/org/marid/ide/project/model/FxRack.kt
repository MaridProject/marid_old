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
import org.marid.model.Argument
import org.marid.model.Initializer
import org.marid.model.Rack

class FxRack : FxEntity(), Rack {

  val factory = SimpleStringProperty(this, "factory", "")
  val name = SimpleStringProperty(this, "name", "")
  val arguments = FXCollections.observableArrayList(FxArgument::observables)
  val outputs = FXCollections.observableArrayList(FxOutput::observables)
  val initializers = FXCollections.observableArrayList(FxInitializer::observables)
  val observables = arrayOf<Observable>(factory, name, arguments, outputs, initializers, resolvedType)

  override fun getArguments(): MutableList<out Argument> = arguments
  override fun getName(): String = this.name.get()
  override fun getInitializers(): MutableList<out Initializer> = initializers
  override fun setName(name: String) = this.name.set(name)
  override fun setFactory(factory: String) = this.factory.set(factory)
  override fun getFactory(): String = this.factory.get()

  override fun addArgument(argument: Argument) {
    arguments.add(argument as FxArgument)
  }

  override fun addInitializer(initializer: Initializer) {
    initializers.add(initializer as FxInitializer)
  }
}
