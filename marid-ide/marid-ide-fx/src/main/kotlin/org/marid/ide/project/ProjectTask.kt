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

import javafx.application.Platform
import javafx.beans.value.ChangeListener
import org.marid.fx.concurrent.FxTask

abstract class ProjectTask<V>(val project: Project) : FxTask<V>() {

  private val progressListener = ChangeListener<Number> { _, _, v ->
    project.Friend().progressWrapper.set(v.toDouble())
  }

  final override fun call(): V {
    Platform.runLater { this.progressProperty().addListener(progressListener) }
    try {
      return callTask()
    } finally {
      Platform.runLater {
        updateProgress(0L, 100L)
        this.progressProperty().removeListener(progressListener)
      }
    }
  }

  abstract fun callTask(): V
}
