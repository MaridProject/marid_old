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

import jdk.dynalink.CallSiteDescriptor;
import jdk.dynalink.support.SimpleRelinkableCallSite;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.marid.runtime.exception.CellarCloseException;
import org.marid.runtime.model.ArgumentConstRef;
import org.marid.runtime.model.ArgumentLiteral;
import org.marid.runtime.model.Cellar;
import org.marid.runtime.model.CellarConstant;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Stream.concat;
import static jdk.dynalink.StandardOperation.CALL;

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
    final var constKey = getName() + "/" + constant.getName();
    if (passed.add(constKey)) {
      try {
        final var libClass = this.winery.classLoader.loadClass(constant.getLib());
        final var callable = winery.linkMethod(libClass, constant.getSelector());
        final var args = new Object[constant.getArguments().size() + 2];
        args[0] = callable;
        args[1] = null;
        for (int i = 0; i < constant.getArguments().size(); i++) {
          final var argument = constant.getArguments().get(i);
          if (argument instanceof ArgumentLiteral) {
            final var literal = (ArgumentLiteral) argument;
            args[i + 2] = literal.getType().converter.apply(literal.getValue(), this.winery.classLoader);
          } else if (argument instanceof ArgumentConstRef) {
            final var ref = (ArgumentConstRef) argument;
            final var cellar = this.winery.getCellar(ref.getCellar());
            final var cellarConstant = cellar.cellar.getConstant(ref.getName());
            args[i + 2] = cellar.getOrCreateConst(cellarConstant, passed);
          } else {
            throw new IllegalArgumentException(
                "Illegal argument [" + i + "] of constant " + constKey + ": " + argument.getClass()
            );
          }
        }
        final var callSite = new SimpleRelinkableCallSite(new CallSiteDescriptor(
            MethodHandles.publicLookup(),
            CALL,
            MethodType.genericMethodType(args.length)
        ));
        constants.put(
            constant.getName(),
            current = winery.linker.link(callSite).dynamicInvoker().invokeWithArguments(args)
        );
        return current;
      } catch (Throwable e) {
        throw new IllegalStateException("Unable to create constant: " + constKey, e);
      }
    } else {
      throw new IllegalStateException(
          concat(passed.stream(), Stream.of(constKey)).collect(joining(",", "Circular constant reference: [", "]"))
      );
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
