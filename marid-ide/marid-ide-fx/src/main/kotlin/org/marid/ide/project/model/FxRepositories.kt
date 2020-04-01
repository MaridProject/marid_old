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

class FxRepositories {

  val items = FXCollections.observableArrayList(FxRepository::observables)
  val observables = arrayOf(items)

  fun load(path: Path) {
    Xmls.read(path) { items.setAll(XmlStreams.elementsByTag(it, "repository").map(::FxRepository).toList()) }
  }

  fun save(path: Path) {
    Xmls.writeFormatted("repositories", { e -> items.forEach { i -> e.child("repository").also(i::writeTo) } }, path)
  }
}
