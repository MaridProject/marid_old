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
import org.marid.runtime.model.Argument;
import org.marid.runtime.model.ArgumentConstRef;
import org.marid.runtime.model.ArgumentLiteral;
import org.marid.runtime.model.ArgumentNull;
import org.marid.runtime.model.ArgumentRef;
import org.marid.runtime.model.Cellar;
import org.marid.runtime.model.CellarConstant;
import org.marid.runtime.model.Rack;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.of;

public class CellarRuntime implements AutoCloseable {

  final WineryRuntime winery;
  final Cellar cellar;
  final ConcurrentHashMap<String, RackRuntime> racks = new ConcurrentHashMap<>();
  final ConcurrentHashMap<String, Object> constants = new ConcurrentHashMap<>();

  CellarRuntime(WineryRuntime winery, Cellar cellar) {
    this.winery = winery;
    this.cellar = cellar;
  }

  Object getOrCreateConst(CellarConstant constant, LinkedHashSet<String> passed) {
    final var k = getName() + "/" + constant.getName();
    if (!passed.add(k)) {
      throw new IllegalStateException(concat(passed.stream(), of(k)).collect(joining(",", "Circular const [", "]")));
    }
    return constants.computeIfAbsent(constant.getName(), name -> {
      try {
        final var libClass = winery.classLoader.loadClass(constant.getFactory());
        final var callable = winery.linkMethod(libClass, constant.getSelector());
        final var args = IntStream.range(0, constant.getArguments().size())
            .mapToObj(i -> {
              final var arg = constant.getArguments().get(i);
              if (arg instanceof ArgumentLiteral) {
                final var literal = (ArgumentLiteral) arg;
                return literal.getType().converter.apply(literal.getValue(), winery.classLoader);
              } else if (arg instanceof ArgumentConstRef) {
                final var ref = (ArgumentConstRef) arg;
                final var cellar = winery.getCellar(ref.getCellar());
                final var cellarConstant = cellar.cellar.getConstant(ref.getName());
                return cellar.getOrCreateConst(cellarConstant, passed);
              } else {
                throw new IllegalArgumentException("Illegal arg[" + i + "] of constant " + k + ": " + arg.getClass());
              }
            })
            .toArray();
        return winery.call(callable, args);
      } catch (Throwable e) {
        throw new IllegalStateException("Unable to create constant " + k, e);
      }
    });
  }

  RackRuntime getOrCreateRack(Rack rack, LinkedHashSet<String> passed) {
    final var k = getName() + "/" + rack.getName();
    if (!passed.add(k)) {
      throw new IllegalStateException(concat(passed.stream(), of(k)).collect(joining(",", "Circular rack [", "]")));
    }
    final var rackRuntime = racks.computeIfAbsent(rack.getName(), name -> {
      try {
        final var args = IntStream.range(0, rack.getArguments().size())
            .mapToObj(i -> {
              try {
                return arg(rack.getArguments().get(i), passed);
              } catch (Throwable e) {
                throw new IllegalArgumentException("Illegal argument [" + i + "] of " + k, e);
              }
            })
            .toArray();
        final var rackClass = winery.classLoader.loadClass(rack.getFactory());
        final var instance = winery.construct(rackClass, args);
        winery.racks.add(Map.entry(getName(), name));
        return new RackRuntime(this, rack, instance);
      } catch (Throwable e) {
        throw new IllegalStateException("Unable to create rack " + getId() + "/" + rack.getName(), e);
      }
    });
    for (final var input : rack.getInputs()) {
      try {
        final var inputArg = arg(input.getArgument(), passed);
        winery.set(rackRuntime.instance, input.getName(), inputArg);
      } catch (Throwable e) {
        throw new IllegalArgumentException("Illegal input " + input.getName() + " of " + k, e);
      }
    }
    for (int i = 0; i < rack.getInitializers().size(); i++) {
      final var initializer = rack.getInitializers().get(i);
      try {
        final var args = IntStream.range(0, initializer.getArguments().size())
            .mapToObj(j -> {
              try {
                return arg(initializer.getArguments().get(j), passed);
              } catch (Throwable e) {
                throw new IllegalArgumentException("Illegal argument [" + j + "] of " + k, e);
              }
            })
            .toArray();
        final var callable = winery.linkMethod(rackRuntime.instance.getClass(), initializer.getName());
        winery.call(callable, args);
      } catch (Throwable e) {
        final var initializerName = "[" + i + "] (" + initializer.getName() + ")";
        throw new IllegalStateException("Unable to invoke initializer " + initializerName + " of " + k, e);
      }
    }
    return rackRuntime;
  }

  private Object arg(Argument arg, LinkedHashSet<String> passed) {
    if (arg instanceof ArgumentLiteral) {
      final var literal = (ArgumentLiteral) arg;
      return literal.getType().converter.apply(literal.getValue(), winery.classLoader);
    } else if (arg instanceof ArgumentConstRef) {
      final var ref = (ArgumentConstRef) arg;
      final var cellar = winery.getCellar(ref.getCellar());
      return cellar.getConstant(ref.getName());
    } else if (arg instanceof ArgumentNull) {
      return null;
    } else if (arg instanceof ArgumentRef) {
      final var ref = (ArgumentRef) arg;
      final var cellar = ref.getCellar() == null ? this : winery.getCellar(ref.getCellar());
      final var cellarRack = cellar.cellar.getRack(ref.getRack());
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
}
