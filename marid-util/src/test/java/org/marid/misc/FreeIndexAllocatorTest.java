package org.marid.misc;

/*-
 * #%L
 * marid-util
 * %%
 * Copyright (C) 2012 - 2019 MARID software development group
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

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

@Tag("normal")
class FreeIndexAllocatorTest {

  @Test
  void test() {
    final var allocator = new FreeIndexAllocator();
    final int[] indices = IntStream.generate(allocator::freeIndex)
        .parallel()
        .limit(10_000)
        .sorted()
        .toArray();
    final int[] withoutDuplicates = Arrays.stream(indices)
        .distinct()
        .toArray();
    assertArrayEquals(withoutDuplicates, indices);
  }
}
