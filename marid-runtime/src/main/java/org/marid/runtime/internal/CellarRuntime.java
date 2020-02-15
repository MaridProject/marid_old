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
import org.marid.runtime.model.LiteralImpl;
import org.marid.runtime.model.NullImpl;
import org.marid.runtime.model.RackImpl;
import org.marid.runtime.model.RefImpl;

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
        final var callable = winery.linkMethod(libClass, constant.getSelector());
        final var args = IntStream.range(0, constant.getArguments().size())
            .mapToObj(i -> {
              final var arg = constant.getArguments().get(i);
              if (arg instanceof LiteralImpl) {
                final var literal = (LiteralImpl) arg;
                return literal.getType().converter.apply(literal.getValue(), winery.classLoader);
              } else if (arg instanceof ConstRefImpl) {
                final var ref = (ConstRefImpl) arg;
                final var cellar = winery.getCellar(ref.getCellar());
                final var cellarConstant = cellar.cellar.getConstants().stream()
                  .filter(c -> c.getName().equals(ref.getRef()))
                  .findFirst()
                  .orElseThrow(() -> new IllegalStateException("No such ref " + ref.getRef()));
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

  RackRuntime getOrCreateRack(RackImpl rack, LinkedHashSet<String> passed) {
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

  Object arg(AbstractArgument arg, LinkedHashSet<String> passed) {
    if (arg instanceof LiteralImpl) {
      final var literal = (LiteralImpl) arg;
      return literal.getType().converter.apply(literal.getValue(), winery.classLoader);
    } else if (arg instanceof ConstRefImpl) {
      final var ref = (ConstRefImpl) arg;
      final var cellar = winery.getCellar(ref.getCellar());
      return cellar.getConstant(ref.getRef());
    } else if (arg instanceof NullImpl) {
      return null;
    } else if (arg instanceof RefImpl) {
      final var ref = (RefImpl) arg;
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
}
