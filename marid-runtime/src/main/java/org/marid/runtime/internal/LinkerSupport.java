package org.marid.runtime.internal;

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
}
