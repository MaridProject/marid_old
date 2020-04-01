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

package org.marid.fx.dialog

import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableValue
import org.marid.fx.action.Fx
import org.marid.fx.i18n.localized
import org.marid.fx.validation.Validation

@Target(AnnotationTarget.PROPERTY)
@Retention
annotation class FxDialogProp(val label: String, val icon: String = "") {
  companion object {
    val FxDialogProp.fx
      get() = Fx(
        text = label.takeUnless { it.isBlank() },
        icon = icon.takeUnless { it.isBlank() }
      )
  }
}

abstract class FxDialogData(title: ObservableValue<String>) {

  val title = SimpleStringProperty()
  val validation = Validation()

  init {
    this.title.bind(title)
  }

  constructor(title: String) : this(title.localized)
}
