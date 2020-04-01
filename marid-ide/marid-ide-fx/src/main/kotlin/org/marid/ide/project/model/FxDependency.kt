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

class FxDependency(group: String, artifact: String, version: String) {

  val group = SimpleStringProperty(this, "group", group)
  val artifact = SimpleStringProperty(this, "artifact", artifact)
  val version = SimpleStringProperty(this, "version", version)

  val observables = arrayOf(this.group, this.artifact, this.version)

  constructor(element: Element) : this(
    group = element["group"],
    artifact = element["artifact"],
    version = element["version"]
  )

  fun writeTo(element: Element) {
    element["group"] = group.get()
    element["artifact"] = artifact.get()
    element["version"] = version.get()
  }

  fun matches(dep: FxDependency): Boolean = dep.group.get() == group.get() && dep.artifact.get() == artifact.get()
  val isBlank get() = group.get().isBlank() || artifact.get().isBlank() || version.get().isBlank()
  fun copyFrom(dep: FxDependency) {
    group.set(dep.group.get())
    artifact.set(dep.artifact.get())
    version.set(dep.version.get())
  }

  operator fun component1(): String = group.get()
  operator fun component2(): String = artifact.get()
  operator fun component3(): String = version.get()
}
