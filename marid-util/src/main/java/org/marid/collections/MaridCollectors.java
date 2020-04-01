package org.marid.collections;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.stream.Collector;

public interface MaridCollectors {

  static <T, E extends List<T>, L extends List<E>> Collector<T, L, L> partition(int size, IntFunction<E> pList, Supplier<L> rList) {
    return new Collector<>() {
      @Override
      public Supplier<L> supplier() {
        return rList;
      }

      @Override
      public BiConsumer<L, T> accumulator() {
        return (a, e) -> {
          if (!a.isEmpty()) {
            final var last = a.get(a.size() - 1);
            if (last.size() < size) {
              last.add(e);
            }
          }
          final var list = pList.apply(size);
          list.add(e);
          a.add(list);
        };
      }

      @Override
      public BinaryOperator<L> combiner() {
        return (l1, l2) -> l1;
      }

      @Override
      public Function<L, L> finisher() {
        return Function.identity();
      }

      @Override
      public Set<Characteristics> characteristics() {
        return Set.of(Characteristics.IDENTITY_FINISH);
      }
    };
  }

  static <T> Collector<T, ArrayList<ArrayList<T>>, ArrayList<ArrayList<T>>> partition(int size) {
    return partition(size, ArrayList::new, ArrayList::new);
  }
}
