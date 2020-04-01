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

import java.util.*

fun <E> MutableList<E>.toTop(index: Int) {
  if (index > 0) {
    val map = this
      .mapIndexed { i, e ->
        e to when {
          i == index -> 0
          i < index -> i + 1
          else -> i
        }
      }
      .fold(IdentityHashMap<E, Int>()) { acc, (e, i) -> acc.also { it[e] = i } }
    sortWith(compareBy(map::get))
  }
}

fun <E> MutableList<E>.toBottom(index: Int) {
  if (index < size - 1) {
    val map = this
      .mapIndexed { i, e ->
        e to when {
          i == index -> size - 1
          i > index -> i - 1
          else -> i
        }
      }
      .fold(IdentityHashMap<E, Int>()) { acc, (e, i) -> acc.also { it[e] = i } }
    sortWith(compareBy(map::get))
  }
}

fun <E> MutableList<E>.up(index: Int) {
  if (index > 0) {
    val map = this
      .mapIndexed { i, e ->
        e to when (i) {
          index -> i - 1
          index - 1 -> i + 1
          else -> i
        }
      }
      .fold(IdentityHashMap<E, Int>()) { acc, (e, i) -> acc.also { it[e] = i } }
    sortWith(compareBy(map::get))
  }
}

fun <E> MutableList<E>.addOrAppend(index: Int, elem: E): Int = when(index) {
  -1 -> {
    val sz = size
    add(elem)
    sz
  }
  else -> {
    add(index, elem)
    index
  }
}
