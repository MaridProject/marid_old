/*-
 * #%L
 * marid-spring
 * %%
 * Copyright (C) 2012 - 2018 MARID software development group
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
package org.marid.spring.orders;

import org.marid.logging.Log;
import org.springframework.core.annotation.Order;

import java.util.Arrays;
import java.util.function.IntFunction;
import java.util.stream.IntStream;

public interface Orders {

  static int index(IntFunction<?> itemFunc, int size) {
    final int[] orders = IntStream.range(0, size)
        .mapToObj(itemFunc)
        .map(v -> v.getClass().getAnnotation(Order.class))
        .mapToInt(o -> o == null ? 0 : o.value())
        .toArray();
    final var caller = Log.WALKER.getCallerClass();
    final var order = caller.isAnnotationPresent(Order.class) ? caller.getAnnotation(Order.class).value() : 0;
    final int index = Arrays.binarySearch(orders, order);
    return index < 0 ? -(index + 1) : index;
  }
}
