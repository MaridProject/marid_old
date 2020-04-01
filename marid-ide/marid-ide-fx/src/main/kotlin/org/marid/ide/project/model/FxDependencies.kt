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

import javafx.collections.FXCollections
import org.marid.fx.xml.child
import org.marid.io.Xmls
import org.marid.xml.XmlStreams
import java.nio.file.Path
import kotlin.streams.toList

class FxDependencies {

  val items = FXCollections.observableArrayList(FxDependency::observables)
  val observables = arrayOf(items)

  fun load(path: Path) {
    Xmls.read(path) { items.setAll(XmlStreams.elementsByTag(it, "dependency").map(::FxDependency).toList()) }
  }

  fun save(path: Path) {
    Xmls.writeFormatted("dependencies", { e -> items.forEach { i -> e.child("dependency").also(i::writeTo) } }, path)
  }

  fun loadDefault() {
    items.removeIf { STANDARD_DEPS.any(it::matches) }
    items.addAll(0, STANDARD_DEPS)
  }

  companion object {
    val STANDARD_DEPS = listOf(
      FxDependency("org.marid", "marid-racks", "\${marid.version}"),
      FxDependency("org.marid", "marid-db", "\${marid.version}"),
      FxDependency("org.marid", "marid-proto", "\${marid.version}")
    )
  }
}
