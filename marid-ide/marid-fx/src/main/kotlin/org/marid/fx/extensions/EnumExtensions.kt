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

import javafx.geometry.Side
import kotlin.math.max

inline val <reified E : Enum<E>> E.progress: Double get() = ordinal.toDouble() / enumValues<E>().size.toDouble()

inline fun <reified E : Enum<E>> E.progress(subProgress: Double): Double {
  val step = 1.0 / enumValues<E>().size.toDouble()
  val value = max(step * subProgress, step)
  return progress + value
}

inline fun <reified E : Enum<E>, reified P : Enum<P>> E.progress(subItem: P): Double = progress(subItem.progress)

val Side.icon get() = when(this) {
  Side.TOP -> "icons/side/top.png"
  Side.BOTTOM -> "icons/side/bottom.png"
  Side.LEFT -> "icons/side/left.png"
  Side.RIGHT -> "icons/side/right.png"
}
