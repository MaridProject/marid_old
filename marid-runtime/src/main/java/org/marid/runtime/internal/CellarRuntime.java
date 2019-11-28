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
import org.jetbrains.annotations.Nullable;
import org.marid.runtime.exception.CellarCloseException;
import org.marid.runtime.model.ArgumentConstRef;
import org.marid.runtime.model.ArgumentLiteral;
import org.marid.runtime.model.Cellar;
import org.marid.runtime.model.CellarConstant;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.of;

public class CellarRuntime implements AutoCloseable {

  final WineryRuntime winery;
  final Cellar cellar;
  final LinkedHashMap<String, RackRuntime> racks = new LinkedHashMap<>();
  final LinkedHashMap<String, Object> constants = new LinkedHashMap<>();

  CellarRuntime(WineryRuntime winery, Cellar cellar) {
    this.winery = winery;
    this.cellar = cellar;
  }

  Object getOrCreateConst(CellarConstant constant, LinkedHashSet<String> passed) {
    var current = constants.get(constant.getName());
    if (current != null) {
      return current;
    }
    final var k = getName() + "/" + constant.getName();
    if (!passed.add(k)) {
      throw new IllegalStateException(concat(passed.stream(), of(k)).collect(joining(",", "Circular const [", "]")));
    }
    try {
      final var libClass = winery.classLoader.loadClass(constant.getLib());
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
      constants.put(constant.getName(), current = winery.call(callable, args));
      return current;
    } catch (Throwable e) {
      throw new IllegalStateException("Unable to create constant: " + k, e);
    }
  }

  public @Nullable Object getConstant(@NotNull String name) {
    return constants.get(name);
  }

  public @NotNull Set<@NotNull String> getConstantNames() {
    return Collections.unmodifiableSet(constants.keySet());
  }

  public @Nullable RackRuntime getRack(@NotNull String name) {
    return racks.get(name);
  }

  public @NotNull Set<@NotNull String> getRackNames() {
    return Collections.unmodifiableSet(racks.keySet());
  }

  public @NotNull String getName() {
    return cellar.getName();
  }

  @Override
  public void close() {
    constants.clear();
    final var rackEntries = new LinkedList<>(racks.entrySet());
    racks.clear();
    final var ex = new CellarCloseException(this);
    for (final var it = rackEntries.descendingIterator(); it.hasNext(); ) {
      final var entry = it.next();
      final var rack = entry.getValue();
      try {
        rack.close();
      } catch (Throwable e) {
        ex.addSuppressed(e);
      } finally {
        it.remove();
      }
    }
    if (ex.getSuppressed().length > 0) {
      throw ex;
    }
  }
}
