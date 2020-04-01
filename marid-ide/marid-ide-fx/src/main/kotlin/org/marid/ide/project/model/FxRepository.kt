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

import javafx.beans.property.SimpleStringProperty
import org.marid.fx.xml.get
import org.marid.fx.xml.set
import org.w3c.dom.Element
import java.util.*

class FxRepository(name: String, url: String) {

  val name = SimpleStringProperty(this, "name", name)
  val url = SimpleStringProperty(this, "url", url)

  val observables = arrayOf(this.name, this.url)

  constructor(element: Element) : this(
    name = element["name"],
    url = element["url"]
  )

  fun writeTo(element: Element) {
    element["name"] = name.get()
    element["url"] = url.get()
  }

  override fun hashCode(): Int = Objects.hash(name.get(), url.get())
  override fun equals(other: Any?): Boolean = other === this || when (other) {
    is FxRepository -> arrayOf(name.get(), url.get()).contentEquals(arrayOf(other.name.get(), other.url.get()))
    else -> false
  }
}
