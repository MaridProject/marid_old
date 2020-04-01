package org.marid.misc;

/*-
 * #%L
 * marid-util
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

import java.util.BitSet;
import java.util.concurrent.Callable;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;

public final class FreeIndexAllocator {

  private final BitSet words = new BitSet();

  public synchronized int freeIndex() {
    final int index = words.nextClearBit(0);
    words.set(index);
    return index;
  }

  public synchronized void free(int index) {
    words.clear(index);
  }

  public void withFreeIndex(IntConsumer consumer) {
    final int index = freeIndex();
    try {
      consumer.accept(index);
    } finally {
      free(index);
    }
  }

  public <E> E callWithFreeIndex(IntFunction<E> callback) {
    final int index = freeIndex();
    try {
      return callback.apply(index);
    } finally {
      free(index);
    }
  }

  public <E> E callWithFreeIndexExceptionally(IntFunction<Callable<E>> callback) throws Exception {
    final int index = freeIndex();
    try {
      return callback.apply(index).call();
    } finally {
      free(index);
    }
  }
}
