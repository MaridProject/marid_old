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
import org.marid.runtime.exception.RackCloseException;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.LinkedHashMap;

import static java.lang.invoke.MethodType.methodType;
import static jdk.dynalink.StandardNamespace.METHOD;
import static jdk.dynalink.StandardOperation.CALL;
import static jdk.dynalink.StandardOperation.GET;

public class RackRuntime implements AutoCloseable {

  public final String name;

  private final CellarRuntime cellar;
  private final Object instance;
  private final LinkedHashMap<String, Object[]> destroyers;

  RackRuntime(CellarRuntime cellar, String name, Object instance, LinkedHashMap<String, Object[]> destroyers) {
    this.cellar = cellar;
    this.name = name;
    this.instance = instance;
    this.destroyers = destroyers;
  }

  @Override
  public void close() {
    final var ex = new RackCloseException(this);
    final var entries = destroyers.entrySet();
    for (final var it = entries.iterator(); it.hasNext(); ) {
      final var entry = it.next();
      final var methodName = entry.getKey();
      final var args = entry.getValue();
      try {
        final var callable = cellar.winery.linker.link(new SimpleRelinkableCallSite(new CallSiteDescriptor(
            MethodHandles.publicLookup(),
            GET.withNamespace(METHOD).named(methodName),
            methodType(Object.class, Object.class)
        ))).dynamicInvoker().bindTo(instance).invoke();

        final var actualArgs = new Object[args.length + 2];
        System.arraycopy(args, 0, actualArgs, 2, args.length);
        actualArgs[0] = callable;
        actualArgs[1] = instance;

        cellar.winery.linker.link(new SimpleRelinkableCallSite(new CallSiteDescriptor(
            MethodHandles.publicLookup(),
            CALL,
            MethodType.genericMethodType(actualArgs.length)
        ))).dynamicInvoker().invoke(actualArgs);
      } catch (Throwable e) {
        ex.addSuppressed(new IllegalStateException("Unable to call " + methodName, e));
      } finally {
        it.remove();
      }
    }
    if (ex.getSuppressed().length > 0) {
      throw ex;
    }
  }
}
