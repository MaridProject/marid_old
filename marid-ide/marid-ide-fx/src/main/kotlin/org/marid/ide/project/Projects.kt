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

package org.marid.ide.project

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.marid.fx.extensions.WARN
import org.marid.fx.extensions.logger
import org.marid.ide.common.Directories
import org.springframework.stereotype.Component
import java.nio.file.Files

@Component
class Projects(
  private val directories: Directories
) {

  private val _items = FXCollections.observableArrayList(Project::observables)
  val items: ObservableList<Project> = FXCollections.unmodifiableObservableList(_items)

  init {
    Files.newDirectoryStream(directories.projectsHome) { Files.isDirectory(it) }.use { dirs ->
      for (dir in dirs) {
        try {
          _items += Project(this, dir.fileName.toString())
        } catch (e: Throwable) {
          logger.WARN("Unable to load {0}", e, dir)
        }
      }
    }
  }

  fun newProject(): Project {
    val project = Project(this)
    _items.add(project)
    return project
  }

  companion object {
    val Project.directories get() = projects.directories
    val Project.writableItems: ObservableList<Project> get() = projects._items
  }
}
