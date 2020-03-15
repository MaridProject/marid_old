package org.marid.runtime.model;

import java.lang.invoke.MethodHandleProxies;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.function.Supplier;

class ModelObjectFactoryFriend {

  private static final WeakHashMap<ModelObjectFactory, HashMap<String, Supplier<?>>> MAP = new WeakHashMap<>();

  static Entity newEntity(ModelObjectFactory factory, String tag) {
    synchronized (MAP) {
      return (Entity) Objects.requireNonNull(MAP.computeIfAbsent(
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
      ).get(tag).get(), () -> "No such entity for tag: " + tag);
    }
  }
}
