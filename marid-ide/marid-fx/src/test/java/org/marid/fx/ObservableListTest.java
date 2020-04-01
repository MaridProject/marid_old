package org.marid.fx;

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

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

@Tag("normal")
class ObservableListTest {

  @Test
  void permutation() {
    final var list = FXCollections.observableArrayList(1, 3, 8, 7, 2, 9);
    final var permutations = new AtomicReference<>(new int[0]);
    list.addListener((ListChangeListener<Integer>) c -> {
      while (c.next()) {
        if (c.wasPermutated()) {
          permutations.set(IntStream.range(c.getFrom(), c.getTo()).map(c::getPermutation).toArray());
        }
      }
    });
    list.sort(Integer::compareTo);
    assertArrayEquals(new int[]{0, 2, 4, 3, 1, 5}, permutations.get());
  }
}
