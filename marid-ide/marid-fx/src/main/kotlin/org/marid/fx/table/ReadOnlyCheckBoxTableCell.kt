/*-
 * #%L
 * marid-fx
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

package org.marid.fx.table

import javafx.scene.control.CheckBox
import javafx.scene.control.TableCell

class ReadOnlyCheckBoxTableCell<S> : TableCell<S, Boolean?>() {

  private val checkBox = object : CheckBox() {
    override fun arm() {}
  }

  init {
    styleClass.add("check-box-table-cell")
    graphic = checkBox
    text = null
  }

  override fun updateItem(item: Boolean?, empty: Boolean) {
    super.updateItem(item, empty)
    if (empty || item == null) {
      checkBox.isVisible = false
    } else {
      checkBox.isVisible = true
      checkBox.isSelected = item
    }
  }
}
