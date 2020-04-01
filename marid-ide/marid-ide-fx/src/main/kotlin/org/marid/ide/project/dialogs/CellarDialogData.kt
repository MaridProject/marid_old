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

package org.marid.ide.project.dialogs

import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.TextField
import org.marid.fx.dialog.FxDialogData
import org.marid.fx.dialog.FxDialogProp
import org.marid.fx.validation.validate
import org.marid.ide.project.model.FxCellar
import org.marid.ide.project.model.FxWinery

class CellarDialogData(winery: FxWinery, cellar: FxCellar?) : FxDialogData("Cellar") {

  val name = SimpleStringProperty()

  @FxDialogProp(label = "Name")
  val nameControl = TextField(cellar?.name?.get() ?: "cellar").also {
    name.bindBidirectional(it.textProperty())
    validation.add(it, name.validate(String::isBlank) { "Value is blank" })
    validation.add(it, name.validate({ v -> winery.cellars.any { c -> c.getName() == v } }) { "Already exists" })
  }

  fun invoke(cellar: FxCellar? = null) = (cellar ?: FxCellar()).also { it.setName(name.get()) }
}
