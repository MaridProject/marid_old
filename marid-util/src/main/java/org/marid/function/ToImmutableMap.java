/*-
 * #%L
 * marid-util
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
package org.marid.function;

import org.marid.misc.Casts;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class ToImmutableMap<K, V> implements Collector<Entry<K, V>, LinkedList<Entry<K, V>>, Map<K, V>> {

  @Override
  public Supplier<LinkedList<Entry<K, V>>> supplier() {
    return LinkedList::new;
  }

  @Override
  public BiConsumer<LinkedList<Entry<K, V>>, Entry<K, V>> accumulator() {
    return LinkedList::add;
  }

  @Override
  public BinaryOperator<LinkedList<Entry<K, V>>> combiner() {
    return (l1, l2) -> l2;
  }

  @Override
  public Function<LinkedList<Entry<K, V>>, Map<K, V>> finisher() {
    return l -> Map.ofEntries(Casts.cast(l.toArray(new Entry[0])));
  }

  @Override
  public Set<Characteristics> characteristics() {
    return EnumSet.of(Characteristics.UNORDERED);
  }
}
