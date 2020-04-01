package org.marid.test.util;

/*-
 * #%L
 * marid-test
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

import java.util.*;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public interface Maps {

  @SafeVarargs
  static <K, V> Map<K, V> mapOf(Entry<K, V>... entries) {
    return Arrays.stream(entries)
        .collect(Collectors.toMap(Entry::getKey, Entry::getValue, (v1, v2) -> v2, LinkedHashMap::new));
  }

  static <K, V> Entry<K, V> e(K k, V v) {
    return new SimpleImmutableEntry<>(k, v);
  }

  static <K, V> Map<K, V> map(K k1, V v1) {
    return Collections.singletonMap(k1, v1);
  }

  static <K, V> Map<K, V> map(K k1, V v1, K k2, V v2) {
    return mapOf(e(k1, v1), e(k2, v2));
  }

  static <K, V> Map<K, V> map(K k1, V v1, K k2, V v2, K k3, V v3) {
    return mapOf(e(k1, v1), e(k2, v2), e(k3, v3));
  }

  static <K, V> Map<K, V> map(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
    return mapOf(e(k1, v1), e(k2, v2), e(k3, v3), e(k4, v4));
  }

  static <K, V> Map<K, V> map(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
    return mapOf(e(k1, v1), e(k2, v2), e(k3, v3), e(k4, v4), e(k5, v5));
  }

  static <K, V> Map<K, V> map(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6) {
    return mapOf(e(k1, v1), e(k2, v2), e(k3, v3), e(k4, v4), e(k5, v5), e(k6, v6));
  }

  static <K, V> Map<K, V> map(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7) {
    return mapOf(e(k1, v1), e(k2, v2), e(k3, v3), e(k4, v4), e(k5, v5), e(k6, v6), e(k7, v7));
  }
}
