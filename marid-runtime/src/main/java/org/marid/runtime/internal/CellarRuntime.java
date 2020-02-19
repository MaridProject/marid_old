package org.marid.runtime.internal;

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

import org.jetbrains.annotations.NotNull;
import org.marid.runtime.model.AbstractArgument;
import org.marid.runtime.model.CellarConstantImpl;
import org.marid.runtime.model.CellarImpl;
import org.marid.runtime.model.ConstRefImpl;
import org.marid.runtime.model.ConstantArgument;
import org.marid.runtime.model.HasVarargs;
import org.marid.runtime.model.LiteralImpl;
import org.marid.runtime.model.NullImpl;
import org.marid.runtime.model.RackImpl;
import org.marid.runtime.model.Ref;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Array;
import java.lang.reflect.Executable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.of;

public class CellarRuntime implements AutoCloseable {

  final WineryRuntime winery;
  final CellarImpl cellar;
  final ConcurrentHashMap<String, RackRuntime> racks = new ConcurrentHashMap<>();
  final ConcurrentHashMap<String, Object> constants = new ConcurrentHashMap<>();

  CellarRuntime(WineryRuntime winery, CellarImpl cellar) {
    this.winery = winery;
    this.cellar = cellar;
  }

  Object getOrCreateConst(CellarConstantImpl constant, LinkedHashSet<String> passed) {
    final var k = getName() + "/" + constant.getName();
    if (!passed.add(k)) {
      throw new IllegalStateException(concat(passed.stream(), of(k)).collect(joining(",", "Circular const [", "]")));
    }
    return constants.computeIfAbsent(constant.getName(), name -> {
      try {
        final var libClass = winery.classLoader.loadClass(constant.getFactory());
        final var method = Arrays.stream(libClass.getMethods())
          .filter(m -> m.getName().equals(constant.getSelector()))
          .filter(m -> matches(constant.getArguments(), m))
          .findFirst()
          .orElseThrow(() -> new NoSuchElementException("No such constant: " + constant));
        final var parameters = method.getParameters();
        final var params = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
          final var p = parameters[i];
          if (p.isVarArgs()) {
            params[i] = typedArray(constant, p.getType().getComponentType(), constant.getArguments().stream()
              .filter(a -> a.getName().equals(p.getName()))
              .map(a -> arg(a, passed, true))
              .toArray());
          } else {
            params[i] = constant.getArguments().stream()
              .filter(a -> a.getName().equals(p.getName()))
              .map(a -> arg(a, passed, true))
              .findFirst()
              .orElseGet(() -> defaultArg(p.getType()));
          }
        }
        return method.invoke(null, params);
      } catch (Throwable e) {
        throw new IllegalStateException("Unable to create constant " + k, e);
      }
    });
  }

  RackRuntime getOrCreateRack(RackImpl rack, LinkedHashSet<String> passed) {
    final var k = getName() + "/" + rack.getName();
    if (!passed.add(k)) {
      throw new IllegalStateException(concat(passed.stream(), of(k)).collect(joining(",", "Circular rack [", "]")));
    }
    final var rackRuntime = racks.computeIfAbsent(rack.getName(), name -> {
      try {
        final var rackClass = winery.classLoader.loadClass(rack.getFactory());
        final var constructor = Arrays.stream(rackClass.getConstructors())
          .filter(c -> matches(rack.getArguments(), c))
          .findFirst()
          .orElseThrow(() -> new NoSuchElementException("No such constructor"));
        final var params = params(rack, rack.getArguments(), constructor, passed);
        final var instance = constructor.newInstance(params);
        winery.racks.add(Map.entry(getName(), name));
        return new RackRuntime(this, rack, instance);
      } catch (Throwable e) {
        throw new IllegalStateException("Unable to create rack " + getId() + "/" + rack.getName(), e);
      }
    });
    for (int i = 0; i < rack.getInitializers().size(); i++) {
      final var initializer = rack.getInitializers().get(i);
      try {
        final var method = Arrays.stream(rackRuntime.instance.getClass().getMethods())
          .filter(m -> m.getName().equals(initializer.getName()))
          .filter(m -> matches(initializer.getArguments(), m))
          .findFirst()
          .orElseThrow(() -> new NoSuchElementException(initializer.getName()));
        final var params = params(initializer, initializer.getArguments(), method, passed);
        method.invoke(rackRuntime.instance, params);
      } catch (Throwable e) {
        final var initializerName = "[" + i + "] (" + initializer.getName() + ")";
        throw new IllegalStateException("Unable to invoke initializer " + initializerName + " of " + k, e);
      }
    }
    return rackRuntime;
  }

  Object arg(ConstantArgument arg, LinkedHashSet<String> passed, boolean create) {
    if (arg instanceof NullImpl) {
      return null;
    } else if (arg instanceof LiteralImpl) {
      final var literal = (LiteralImpl) arg;
      return literal.getType().converter.apply(literal.getValue(), winery.classLoader);
    } else if (arg instanceof ConstRefImpl) {
      final var ref = (ConstRefImpl) arg;
      final var cellar = winery.getCellar(ref.getCellar());
      if (create) {
        final var cellarConstant = cellar.cellar.getConstants().stream()
          .filter(c -> c.getName().equals(ref.getRef()))
          .findFirst()
          .orElseThrow(() -> new IllegalStateException("No such ref " + ref.getRef()));
        return cellar.getOrCreateConst(cellarConstant, passed);
      } else {
        return cellar.getConstant(ref.getRef());
      }
    } else {
      throw new IllegalArgumentException("Illegal arg[" + arg.getName() + ": " + arg.getClass());
    }
  }

  Object arg(AbstractArgument arg, LinkedHashSet<String> passed) {
    if (arg instanceof ConstantArgument) {
      return arg((ConstantArgument) arg, passed, false);
    } else if (arg instanceof Ref) {
      final var ref = (Ref) arg;
      final var cellar = ref.getCellar() == null ? this : winery.getCellar(ref.getCellar());
      final var cellarRack = cellar.cellar.getRacks().stream()
        .filter(r -> r.getName().equals(ref.getRack()))
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("No such rack " + ref.getRack()));
      final var cellarRackRuntime = cellar.getOrCreateRack(cellarRack, passed);
      try {
        return winery.get(cellarRackRuntime.instance, ref.getRef());
      } catch (RuntimeException | Error e) {
        throw e;
      } catch (Throwable e) {
        throw new IllegalStateException(e);
      }
    } else {
      throw new IllegalArgumentException("Illegal argument " + arg.getClass());
    }
  }

  public @NotNull Object getConstant(@NotNull String name) {
    return Objects.requireNonNull(constants.get(name), () -> "No such constant " + cellar.getName() + "/" + name);
  }

  public @NotNull Set<@NotNull String> getConstantNames() {
    return Collections.unmodifiableSet(constants.keySet());
  }

  public @NotNull RackRuntime getRack(@NotNull String name) {
    return Objects.requireNonNull(racks.get(name), () -> "No such rack " + cellar.getName() + "/" + name);
  }

  public @NotNull Set<@NotNull String> getRackNames() {
    return Collections.unmodifiableSet(racks.keySet());
  }

  public @NotNull String getName() {
    return cellar.getName();
  }

  public @NotNull String getId() {
    return winery.getId() + "/" + getName();
  }

  @Override
  public @NotNull String toString() {
    return getId();
  }

  @Override
  public void close() {
    constants.clear();
    racks.clear();
  }

  private Object[] params(HasVarargs va, ArrayList<AbstractArgument> arguments, Executable executable, LinkedHashSet<String> passed) {
    return Arrays.stream(executable.getParameters())
      .map(p -> {
        if (p.isVarArgs()) {
          return typedArray(va, p.getType().getComponentType(), arguments.stream()
            .filter(a -> a.getName().equals(p.getName()))
            .map(a -> arg(a, passed))
            .toArray());
        } else {
          return arguments.stream()
            .filter(a -> a.getName().equals(p.getName()))
            .map(a -> arg(a, passed))
            .findFirst()
            .orElseGet(() -> defaultArg(p.getType()));
        }
      })
      .toArray();
  }

  private static Object defaultArg(Class<?> targetClass) {
    try {
      return MethodHandles.zero(targetClass).invoke();
    } catch (Throwable e) {
      throw new IllegalStateException(e);
    }
  }

  private static boolean matches(ArrayList<? extends AbstractArgument> list, Executable method) {
    OUTER:
    for (final var a : list) {
      for (final var p : method.getParameters()) {
        if (p.getName().equals(a.getName())) {
          continue OUTER;
        }
      }
      return false;
    }
    return true;
  }

  private Object typedArray(HasVarargs va, Class<?> elementType, Object[] args) {
    if (va.getVarargType() != null && va.getVarargType().isBlank()) {
      final var methodType = MethodType.fromMethodDescriptorString(va.getVarargType(), winery.classLoader);
      elementType = methodType.returnType();
    } else {
      Class<?> c = null;
      for (final var a : args) {
        if (a == null) {
          continue;
        }
        if (c == null) {
          c = a.getClass();
        } else if (a.getClass().isAssignableFrom(c)) {
          c = a.getClass();
        }
      }
      elementType = c == null ? elementType : c;
    }
    final var array = Array.newInstance(elementType, args.length);
    for (int i = 0; i < args.length; i++) {
      Array.set(array, i, args[i]);
    }
    return array;
  }
}
