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

package org.marid.idelib

import javafx.event.ActionEvent
import org.marid.fx.action.Fx
import org.marid.misc.Annotations
import org.marid.runtime.annotation.Description
import org.marid.runtime.annotation.Icon
import org.marid.runtime.annotation.Title
import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Constructor

data class Tid(val title: String, val icon: String, val description: String) {

  fun fx(action: (ActionEvent) -> Unit): Fx = Fx(
    text = title,
    icon = icon,
    description = description.takeUnless { it.isBlank() },
    h = action
  )

  val fx
    get() = Fx(
      text = title,
      icon = icon,
      description = description.takeUnless { it.isBlank() }
    )

  companion object {
    fun from(pkg: AnnotatedElement, defaultTitle: String, defaultIcon: String): Tid {
      val (title, icon, description) = from0(pkg)
      return Tid(title ?: defaultTitle, icon ?: defaultIcon, description ?: "")
    }

    fun from(constructor: Constructor<*>, defaultTitle: String, defaultIcon: String): Tid {
      val (cot, coi, cod) = from0(constructor)
      val (ct, ci, cd) = from0(constructor.declaringClass)
      return Tid(cot ?: ct ?: defaultTitle, coi ?: ci ?: defaultIcon, cod ?: cd ?: "")
    }

    private fun from0(element: AnnotatedElement): Array<String?> {
      var title: String? = null
      var icon: String? = null
      var description: String? = null
      Annotations.fetch(element).forEach {
        when (it) {
          is Title -> if (title == null) title = it.value
          is Icon -> if (icon == null) icon = it.value
          is Description -> if (description == null) description = it.value
          else -> {
          }
        }
      }
      return arrayOf(title, icon, description)
    }
  }
}
