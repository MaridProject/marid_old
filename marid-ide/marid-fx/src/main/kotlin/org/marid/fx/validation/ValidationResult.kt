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

package org.marid.fx.validation

import javafx.beans.value.ObservableBooleanValue
import org.marid.fx.extensions.mapObject
import java.util.logging.Level

data class ValidationResult(val level: Level, val message: String) {
  constructor() : this(Level.ALL, "")
  constructor(message: String) : this(Level.SEVERE, message)
}

fun ObservableBooleanValue.validate(message: String, level: Level = Level.SEVERE) =
  mapObject { if (it) ValidationResult(level, message) else ValidationResult() }
