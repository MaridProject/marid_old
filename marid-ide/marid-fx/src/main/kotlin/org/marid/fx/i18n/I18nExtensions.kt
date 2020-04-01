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

import javafx.beans.Observable
import javafx.beans.binding.Bindings
import javafx.beans.binding.StringBinding
import javafx.beans.value.ObservableValue
import java.util.concurrent.Callable

val String.localized: StringBinding
  get() = Bindings.createStringBinding(Callable {
    val bundle = I18n.textsBundle()
    try {
      bundle.getString(this)
    } catch (e: Throwable) {
      this
    }
  }, I18n.locale)

fun String.localized(vararg args: Any?): StringBinding {
  val observables = args.flatMap { if (it is Observable) listOf(it) else emptyList() }.toTypedArray()
  return Bindings.createStringBinding(Callable { i18n(*args) }, *observables, I18n.locale)
}

fun String.i18n(vararg args: Any?): String {
  val bundle = I18n.textsBundle()
  val format = try {
    bundle.getString(this)
  } catch (e: Throwable) {
    this
  }
  return if (args.isEmpty()) {
    format
  } else {
    try {
      val extractedArgs = args.map { if (it is ObservableValue<*>) it.value else it }.toTypedArray()
      String.format(format, *extractedArgs)
    } catch (e: Throwable) {
      format
    }
  }
}
