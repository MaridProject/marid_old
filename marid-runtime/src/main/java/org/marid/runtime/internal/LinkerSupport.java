package org.marid.runtime.internal;

import jdk.dynalink.CallSiteDescriptor;
import jdk.dynalink.DynamicLinker;
import jdk.dynalink.DynamicLinkerFactory;
import jdk.dynalink.beans.BeansLinker;
import jdk.dynalink.beans.StaticClass;
import jdk.dynalink.support.SimpleRelinkableCallSite;

import static java.lang.invoke.MethodHandles.publicLookup;
import static java.lang.invoke.MethodType.methodType;
import static jdk.dynalink.StandardNamespace.METHOD;
import static jdk.dynalink.StandardOperation.GET;

abstract class LinkerSupport {

  final DynamicLinker linker;

  LinkerSupport() {
    final var linkerFactory = new DynamicLinkerFactory();
    linkerFactory.setPrioritizedLinker(new BeansLinker());
    linkerFactory.setFallbackLinkers();
    this.linker = linkerFactory.createLinker();
  }

  Object linkMethod(Class<?> type, String name) throws Throwable {
    final var callSiteDescriptor = new CallSiteDescriptor(
        publicLookup(),
        GET.withNamespace(METHOD).named(name),
        methodType(Object.class, StaticClass.class)
    );
    final var callSite = new SimpleRelinkableCallSite(callSiteDescriptor);
    return linker.link(callSite).dynamicInvoker().bindTo(StaticClass.forClass(type)).invoke();
  }
}
