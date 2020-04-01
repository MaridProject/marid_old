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

package org.marid.fx.i18n

import org.marid.fx.extensions.pref
import java.lang.Thread.currentThread
import java.util.*
import java.util.ResourceBundle.Control.FORMAT_PROPERTIES
import java.util.ResourceBundle.Control.getControl
import java.util.ResourceBundle.getBundle

object I18n {

  private val control = getControl(FORMAT_PROPERTIES)
  fun textsBundle(): ResourceBundle = getBundle("texts", locale.get(), currentThread().contextClassLoader, control)

  val locale = pref("locale", Locale.getDefault())
}
