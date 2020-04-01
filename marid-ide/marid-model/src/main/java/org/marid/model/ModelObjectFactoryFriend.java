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

import org.w3c.dom.Element;

import java.lang.invoke.MethodHandleProxies;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.WeakHashMap;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

class ModelObjectFactoryFriend {

  private static final WeakHashMap<ModelObjectFactory, HashMap<String, Supplier<?>>> MAP = new WeakHashMap<>();

  static Entity newEntity(ModelObjectFactory factory, String tag) {
    synchronized (MAP) {
      return (Entity) MAP.computeIfAbsent(
        factory,
        f -> {
          final var map = new HashMap<String, Supplier<?>>();
          try {
            for (final var m : ModelObjectFactory.class.getMethods()) {
              if (m.getParameterCount() == 0 && Entity.class.isAssignableFrom(m.getReturnType())) {
                final var instance = (Entity) m.invoke(factory);
                final var handle = MethodHandles.publicLookup().unreflect(m).bindTo(factory);
                final var supplier = MethodHandleProxies.asInterfaceInstance(Supplier.class, handle);
                map.put(instance.tag(), supplier);
              }
            }
            return map;
          } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
          }
        }
      ).get(tag).get();
    }
  }

  static Stream<Element> children(Element element) {
    final var list = element.getChildNodes();
    return IntStream.range(0, list.getLength())
      .mapToObj(list::item)
      .filter(Element.class::isInstance)
      .map(Element.class::cast);
  }
}
