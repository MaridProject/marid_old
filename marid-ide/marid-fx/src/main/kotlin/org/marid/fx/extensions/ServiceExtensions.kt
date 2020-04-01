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

package org.marid.fx.extensions

import javafx.beans.Observable
import javafx.concurrent.Worker
import javafx.scene.image.Image

fun Worker.State.icon(width: Int = 0, height: Int = 0): Image = when (this) {
    Worker.State.CANCELLED -> Image("icons/worker/cancelled.png", width.toDouble(), height.toDouble(), true, true)
    Worker.State.FAILED -> Image("icons/worker/failed.png", width.toDouble(), height.toDouble(), true, true)
    Worker.State.SUCCEEDED -> Image("icons/worker/success.png", width.toDouble(), height.toDouble(), true, true)
    Worker.State.READY -> Image("icons/worker/ready.png", width.toDouble(), height.toDouble(), true, true)
    Worker.State.RUNNING -> Image("icons/worker/running.png", width.toDouble(), height.toDouble(), true, true)
    Worker.State.SCHEDULED -> Image("icons/worker/scheduled.png", width.toDouble(), height.toDouble(), true, true)
  }

val Worker<*>.observables: Array<Observable> get() = arrayOf(
  stateProperty(),
  valueProperty(),
  workDoneProperty(),
  totalWorkProperty(),
  progressProperty(),
  titleProperty(),
  runningProperty(),
  messageProperty()
)
