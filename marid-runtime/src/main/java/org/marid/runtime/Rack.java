package org.marid.runtime;

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

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Rack {

  public static final StackWalker STACK_WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
  private static final ThreadLocal<RackHolder> RACK_HOLDER_TL = ThreadLocal.withInitial(RackHolder::new);

  protected static void register() {
    final var caller = STACK_WALKER.getCallerClass();

    final var list = Arrays.stream(caller.getMethods())
        .filter(m -> m.isAnnotationPresent(Destructor.class))
        .filter(AccessibleObject::trySetAccessible)
        .filter(m -> m.getParameterCount() == 0)
        .filter(m -> Modifier.isStatic(m.getModifiers()))
        .sorted(Comparator.comparingInt(m -> m.getAnnotation(Destructor.class).order()))
        .flatMap(m -> {
          try {
            return Stream.of(MethodHandles.publicLookup().unreflect(m));
          } catch (IllegalAccessException e) {
            return Stream.empty();
          }
        })
        .collect(Collectors.toList());

    RACK_HOLDER_TL.get().destructors.addAll(0, list);
  }

  static void clean() {
    final var exception = new IllegalStateException("Unable to clean instances");

    for (final var handle : RACK_HOLDER_TL.get().destructors) {
      try {
        handle.invokeExact();
      } catch (Throwable e) {
        exception.addSuppressed(e);
      }
    }

    if (exception.getSuppressed().length > 0) {
      throw exception;
    }
  }

  private static class RackHolder {

    private final ArrayList<MethodHandle> destructors = new ArrayList<>();
  }
}
