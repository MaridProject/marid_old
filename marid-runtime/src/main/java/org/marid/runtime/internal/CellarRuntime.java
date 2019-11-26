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
import jdk.dynalink.beans.StaticClass;
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
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.stream.Stream;

import static java.lang.invoke.MethodType.methodType;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Stream.concat;
import static jdk.dynalink.StandardNamespace.METHOD;
import static jdk.dynalink.StandardOperation.CALL;
import static jdk.dynalink.StandardOperation.GET;

public class CellarRuntime implements AutoCloseable {

  public final String name;

  final WineryRuntime winery;
  final LinkedHashMap<String, RackRuntime> racks = new LinkedHashMap<>();
  final LinkedHashMap<String, Object> constants = new LinkedHashMap<>();

  CellarRuntime(WineryRuntime winery, Cellar cellar) {
    this.winery = winery;
    this.name = cellar.getName();
  }

  private Object getOrCreateConst(Cellar cellar, String name, LinkedHashSet<String> passed) {
    return constants.computeIfAbsent(name, n -> getOrCreateConst(cellar, cellar.getConstant(name), passed));
  }

  Object getOrCreateConst(Cellar cellar, CellarConstant constant, LinkedHashSet<String> passed) {
    return constants.computeIfAbsent(constant.getName(), name -> {
      final var constKey = cellar.getName() + "/" + name;
      if (passed.add(constKey)) {
        try {
          final var libClass = this.winery.classLoader.loadClass(constant.getLib());
          final var callable = this.winery.linker.link(new SimpleRelinkableCallSite(new CallSiteDescriptor(
              MethodHandles.publicLookup(),
              GET.withNamespace(METHOD).named(constant.getSelector()),
              methodType(Object.class, StaticClass.class)
          ))).dynamicInvoker().bindTo(StaticClass.forClass(libClass)).invoke();
          final var args = new Object[constant.getArguments().size() + 2];
          args[0] = callable;
          args[1] = StaticClass.forClass(libClass);
          for (int i = 0; i < constant.getArguments().size(); i++) {
            final var argument = constant.getArguments().get(i);
            if (argument instanceof ArgumentLiteral) {
              final var literal = (ArgumentLiteral) argument;
              args[i + 2] = literal.getType().converter.apply(literal.getValue(), this.winery.classLoader);
            } else if (argument instanceof ArgumentConstRef) {
              final var ref = (ArgumentConstRef) argument;
              final var tCellar = winery.winery.getCellar(ref.getCellar());
              final var tCellarRuntime = this.winery.cellars.get(ref.getCellar());
              args[i + 2] = tCellarRuntime.getOrCreateConst(tCellar, ref.getName(), passed);
            } else {
              throw new IllegalArgumentException(
                  "Illegal argument [" + i + "] of constant " + constKey + ": " + argument.getClass()
              );
            }
          }
          return this.winery.linker.link(new SimpleRelinkableCallSite(new CallSiteDescriptor(
              MethodHandles.publicLookup(),
              CALL,
              MethodType.genericMethodType(args.length)
          ))).dynamicInvoker().invokeWithArguments(args);
        } catch (Throwable e) {
          throw new IllegalStateException("Unable to create constant: " + constKey, e);
        }
      } else {
        throw new IllegalStateException(
            concat(passed.stream(), Stream.of(constKey)).collect(joining(",", "Circular constant reference: [", "]"))
        );
      }
    });
  }

  public @Nullable Object getConstant(@NotNull String name) {
    return constants.get(name);
  }

  public @Nullable RackRuntime getRack(@NotNull String name) {
    return racks.get(name);
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
