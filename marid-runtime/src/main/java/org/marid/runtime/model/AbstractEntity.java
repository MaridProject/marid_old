package org.marid.runtime.model;

/*-
 * #%L
 * marid-runtime
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

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class AbstractEntity implements Entity {
  @Override
  public final int hashCode() {
    try {
      int code = 0;
      final var beanInfo = Introspector.getBeanInfo(getClass(), Object.class);
      for (final var property : beanInfo.getPropertyDescriptors()) {
        if (property.getReadMethod() != null) {
          final var getter = property.getReadMethod();
          if (getter != null && Entity.class.isAssignableFrom(getter.getDeclaringClass())) {
            final var object = getter.invoke(this);
            code = code * 31 + Objects.hashCode(object);
          }
        }
      }
      return code;
    } catch (IntrospectionException | ReflectiveOperationException e) {
      throw new IllegalStateException(e);
    }
  }

  @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
  @Override
  public final boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    final var thisInterfaces = Arrays.stream(getClass().getInterfaces())
      .filter(Entity.class::isAssignableFrom)
      .collect(Collectors.toUnmodifiableSet());
    final var thatInterfaces = Arrays.stream(getClass().getInterfaces())
      .filter(Entity.class::isAssignableFrom)
      .collect(Collectors.toUnmodifiableSet());
    return thisInterfaces.stream().allMatch(i -> thatInterfaces.stream().anyMatch(i::isAssignableFrom))
      && thatInterfaces.stream().allMatch(i -> thisInterfaces.stream().anyMatch(i::isAssignableFrom));
  }

  @Override
  public final String toString() {
    final var map = new LinkedHashMap<String, String>();
    try {
      final var beanInfo = Introspector.getBeanInfo(getClass(), Object.class);
      for (final var property : beanInfo.getPropertyDescriptors()) {
        if (property.getReadMethod() != null) {
          final var getter = property.getReadMethod();
          if (getter != null && Entity.class.isAssignableFrom(getter.getDeclaringClass())) {
            final var object = getter.invoke(this);
            map.put(property.getName(), Objects.toString(object));
          }
        }
      }
    } catch (IntrospectionException | ReflectiveOperationException e) {
      throw new IllegalStateException(e);
    }
    final var entityType = Arrays.stream(getClass().getInterfaces())
      .filter(Entity.class::isAssignableFrom)
      .findFirst()
      .map(Class::getSimpleName)
      .orElseThrow();
    return entityType + map;
  }
}
