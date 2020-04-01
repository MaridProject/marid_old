package org.marid.fx

import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

@Tag("normal")
class CollectionTest {

  @Test
  fun sortSubList() {
    val permutations = ArrayList<List<Int>>()
    val list = FXCollections.observableArrayList(1, 7, 2, 10, 3, 11);

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

    list.addListener(ListChangeListener { c ->
      while (c.next()) {
        if (c.wasPermutated()) {
          permutations += (c.from until c.to).map { c.getPermutation(it) }
        }
      }
    })
    list.subList(2, 5).sortWith(compareBy { it })
    assertEquals(0, permutations.size)
  }
}
