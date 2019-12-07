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
import jdk.dynalink.DynamicLinker;
import jdk.dynalink.DynamicLinkerFactory;
import jdk.dynalink.StandardNamespace;
import jdk.dynalink.StandardOperation;
import jdk.dynalink.beans.BeansLinker;
import jdk.dynalink.beans.StaticClass;
import jdk.dynalink.support.SimpleRelinkableCallSite;

import java.lang.invoke.MethodType;

import static java.lang.invoke.MethodHandles.publicLookup;

abstract class LinkerSupport {

  final DynamicLinker linker;

  LinkerSupport() {
    final var linkerFactory = new DynamicLinkerFactory();
    linkerFactory.setPrioritizedLinker(new BeansLinker());
    linkerFactory.setFallbackLinkers();
    this.linker = linkerFactory.createLinker();
  }

  Object linkMethod(Class<?> type, String name) throws Throwable {
    final var methodType = MethodType.methodType(Object.class, StaticClass.class);
    final var op = StandardOperation.GET.withNamespace(StandardNamespace.METHOD).named(name);
    final var callSiteDescriptor = new CallSiteDescriptor(publicLookup(), op, methodType);
    final var callSite = new SimpleRelinkableCallSite(callSiteDescriptor);
    return linker.link(callSite).dynamicInvoker().bindTo(StaticClass.forClass(type)).invokeExact();
  }

  Object call(Object callable, Object... args) throws Throwable {
    final var methodType = MethodType.genericMethodType(args.length + 2);
    final var callSiteDescriptor = new CallSiteDescriptor(publicLookup(), StandardOperation.CALL, methodType);
    final var callSite = new SimpleRelinkableCallSite(callSiteDescriptor);
    return linker.link(callSite).dynamicInvoker().bindTo(callable).bindTo(null).invokeWithArguments(args);
  }

  Object construct(Class<?> type, Object... args) throws Throwable {
    final var methodType = MethodType.genericMethodType(args.length + 1);
    final var callSiteDescriptor = new CallSiteDescriptor(publicLookup(), StandardOperation.NEW, methodType);
    final var callSite = new SimpleRelinkableCallSite(callSiteDescriptor);
    return linker.link(callSite).dynamicInvoker().bindTo(StaticClass.forClass(type)).invokeWithArguments(args);
  }

  Object get(Object target, String name) throws Throwable {
    final var methodType = MethodType.methodType(Object.class, Object.class);
    final var op = StandardOperation.GET.withNamespace(StandardNamespace.PROPERTY).named(name);
    final var callSiteDescriptor = new CallSiteDescriptor(publicLookup(), op, methodType);
    final var callSite = new SimpleRelinkableCallSite(callSiteDescriptor);
    return linker.link(callSite).dynamicInvoker().bindTo(target).invokeExact();
  }

  void set(Object target, String name, Object value) throws Throwable {
    final var methodType = MethodType.methodType(void.class, Object.class, Object.class);
    final var op = StandardOperation.SET.withNamespace(StandardNamespace.PROPERTY).named(name);
    final var callSiteDescriptor = new CallSiteDescriptor(publicLookup(), op, methodType);
    final var callSite = new SimpleRelinkableCallSite(callSiteDescriptor);
    linker.link(callSite).dynamicInvoker().bindTo(target).invokeExact(value);
  }
}
