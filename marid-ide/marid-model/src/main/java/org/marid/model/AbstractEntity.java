package org.marid.model;

/*-
 * #%L
 * marid-model
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

import org.jetbrains.annotations.Nullable;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.util.LinkedHashMap;
import java.util.Objects;

public abstract class AbstractEntity implements Entity {
  @Override
  public final int hashCode() {
    try {
      int code = 0;
      final var beanInfo = Introspector.getBeanInfo(getInterface(getClass()));
      for (final var property : beanInfo.getPropertyDescriptors()) {
        if (property.getReadMethod() != null) {
          final var getter = property.getReadMethod();
          if (getter != null) {
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

  @Override
  public final boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    final var itf = getInterface(getClass());
    if (itf != getInterface(obj.getClass())) {
      return false;
    }
    try {
      final var beanInfo = Introspector.getBeanInfo(itf);
      for (final var property : beanInfo.getPropertyDescriptors()) {
        if (property.getReadMethod() != null) {
          final var getter = property.getReadMethod();
          if (getter != null) {
            final var o1 = getter.invoke(this);
            final var o2 = getter.invoke(obj);
            if (!Objects.equals(o1, o2)) {
              return false;
            }
          }
        }
      }
      return true;
    } catch (IntrospectionException | ReflectiveOperationException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public final String toString() {
    final var map = new LinkedHashMap<String, String>();
    try {
      final var beanInfo = Introspector.getBeanInfo(getInterface(getClass()));
      for (final var property : beanInfo.getPropertyDescriptors()) {
        if (property.getReadMethod() != null) {
          final var getter = property.getReadMethod();
          if (getter != null) {
            final var object = getter.invoke(this);
            map.put(property.getName(), Objects.toString(object));
          }
        }
      }
    } catch (IntrospectionException | ReflectiveOperationException e) {
      throw new IllegalStateException(e);
    }
    return getInterface(getClass()).getSimpleName() + map;
  }

  @Override
  public ModelObjectFactory modelObjectFactory() {
    return ModelObjectFactory.FACTORY;
  }

  private Class<?> getInterface(@Nullable Class<?> c) {
    if (c == null || c == Object.class) {
      throw new IllegalStateException();
    }
    for (final var itf : c.getInterfaces()) {
      if (Entity.class.isAssignableFrom(itf)) {
        return itf;
      }
    }
    return getInterface(c.getSuperclass());
  }
}
