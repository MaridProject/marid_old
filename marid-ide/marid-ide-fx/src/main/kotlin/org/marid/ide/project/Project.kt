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

import com.google.common.primitives.Longs
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.property.ReadOnlyBooleanWrapper
import javafx.beans.property.ReadOnlyDoubleProperty
import javafx.beans.property.ReadOnlyDoubleWrapper
import org.marid.fx.extensions.INFO
import org.marid.fx.extensions.WARN
import org.marid.fx.extensions.callFx
import org.marid.fx.extensions.runFx
import org.marid.fx.i18n.localized
import org.marid.ide.project.Projects.Companion.directories
import org.marid.ide.project.Projects.Companion.writableItems
import org.marid.ide.project.model.FxDependencies
import org.marid.ide.project.model.FxRepositories
import org.marid.ide.project.model.FxRepository
import org.marid.ide.project.model.FxWinery
import org.marid.io.Xmls
import org.springframework.util.FileSystemUtils
import java.lang.System.currentTimeMillis
import java.nio.file.Files
import java.util.Base64.getEncoder
import java.util.concurrent.locks.ReentrantReadWriteLock
import java.util.logging.Logger
import kotlin.concurrent.read
import kotlin.concurrent.write

class Project(val projects: Projects, val id: String) {

  constructor(projects: Projects) : this(projects, getEncoder().encodeToString(Longs.toByteArray(currentTimeMillis())))

  val winery = FxWinery()
  val repositories = FxRepositories()
  val dependencies = FxDependencies()
  val observables = winery.observables + repositories.observables + dependencies.observables

  val directory = directories.projectsHome.resolve(id)
  val wineryFile = directory.resolve("winery.xml")
  val repositoriesFile = directory.resolve("repositories.xml")
  val dependenciesFile = directory.resolve("dependencies.xml")
  val resourcesDirectory = directory.resolve("resources")
  val sourcesDirectory = directory.resolve("sources")
  val classesDirectory = directory.resolve("classes")
  val depsDirectory = directory.resolve("deps")
  val runtimeDirectory = directory.resolve("runtime")
  val cacheDirectory = directory.resolve("cache")
  val cacheDepsDirectory = cacheDirectory.resolve("deps")
  val logger = Logger.getLogger(id)

  private val lockedProperty = ReadOnlyBooleanWrapper(this, "locked")
  private val progressProperty = ReadOnlyDoubleWrapper(this, "progress", 0.0)
  private val lock = ReentrantReadWriteLock()

  init {
    val existing = Files.isDirectory(directory)

    if (!existing) {
      winery.name.set("New project %d".localized(projects.items.size + 1).get())
    }

    Files.createDirectories(resourcesDirectory)
    Files.createDirectories(classesDirectory)
    Files.createDirectories(sourcesDirectory)
    Files.createDirectories(depsDirectory)
    Files.createDirectories(runtimeDirectory)
    Files.createDirectories(cacheDepsDirectory)

    load()

    val central = FxRepository("central", "https://repo1.maven.org/maven2/")
    if (central !in repositories.items) {
      repositories.items += central
    }

    if (!existing) {
      save()
    }
  }

  private fun load() {
    if (Files.isRegularFile(wineryFile)) Xmls.read(wineryFile) { winery.readFrom(it) }
    if (Files.isRegularFile(repositoriesFile)) repositories.load(repositoriesFile)
    if (Files.isRegularFile(dependenciesFile)) dependencies.load(dependenciesFile)

    if (dependencies.items.isEmpty()) {
      dependencies.loadDefault()
    }
  }

  fun save() {
    Xmls.writeFormatted("winery", { winery.writeTo(it) }, wineryFile)
    repositories.save(repositoriesFile)
    dependencies.save(dependenciesFile)
  }

  fun delete() {
    writableItems -= this
    if (FileSystemUtils.deleteRecursively(directory)) {
      logger.INFO("Project {0} deleted", id)
    } else {
      logger.WARN("Project {0} does not exist", id)
    }
  }

  fun <R> withRead(callback: (Project) -> R): R = lock.read { callback(this) }
  fun <R> withWrite(callback: (Project) -> R): R {
    callFx { lockedProperty.set(true) }.get()
    try {
      return lock.write { callback(this) }
    } finally {
      runFx { lockedProperty.set(false) }
    }
  }

  val progress: ReadOnlyDoubleProperty get() = progressProperty.readOnlyProperty
  val locked: ReadOnlyBooleanProperty get() = lockedProperty.readOnlyProperty

  override fun hashCode(): Int = id.hashCode()
  override fun equals(other: Any?): Boolean = (other === this) || other is Project && other.id == id

  inner class Friend {
    val progressWrapper get() = progressProperty
  }
}
