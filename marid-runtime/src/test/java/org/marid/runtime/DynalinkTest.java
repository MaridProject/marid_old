package org.marid.runtime;

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
import jdk.dynalink.beans.BeansLinker;
import jdk.dynalink.beans.StaticClass;
import jdk.dynalink.support.SimpleRelinkableCallSite;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.marid.runtime.test.impl.TestBean;

import static java.lang.invoke.MethodHandles.publicLookup;
import static java.lang.invoke.MethodType.methodType;
import static jdk.dynalink.StandardNamespace.METHOD;
import static jdk.dynalink.StandardNamespace.PROPERTY;
import static jdk.dynalink.StandardOperation.CALL;
import static jdk.dynalink.StandardOperation.GET;
import static jdk.dynalink.StandardOperation.NEW;
import static jdk.dynalink.StandardOperation.SET;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("normal")
class DynalinkTest {

  private final DynamicLinker linker;

  public DynalinkTest() {
    final var linkerFactory = new DynamicLinkerFactory();
    linkerFactory.setPrioritizedLinker(new BeansLinker());
    linker = linkerFactory.createLinker();
  }

  @Test
  void get() throws Throwable {
    final var op = GET.withNamespace(PROPERTY).named("x");
    final var csd = new CallSiteDescriptor(publicLookup(), op, methodType(Object.class, Object.class));

    final var site = linker.link(new SimpleRelinkableCallSite(csd));
    final var mh = site.dynamicInvoker().bindTo(new TestBean());

    assertEquals("x", mh.invoke());
  }

  @Test
  void constructor() throws Throwable {
    final var op = NEW.named("new");
    final var csd = new CallSiteDescriptor(publicLookup(), op, methodType(Object.class, StaticClass.class));

    final var site = linker.link(new SimpleRelinkableCallSite(csd));
    final var mh = site.dynamicInvoker();

    assertTrue(mh.invoke(StaticClass.forClass(TestBean.class)) instanceof TestBean);
  }

  @Test
  void setter() throws Throwable {
    final var op = SET.withNamespace(PROPERTY).named("length");
    final var csd = new CallSiteDescriptor(publicLookup(), op, methodType(void.class, Object.class, Object.class));

    final var builder = new StringBuilder();
    final var site = linker.link(new SimpleRelinkableCallSite(csd));
    final var mh = site.dynamicInvoker().bindTo(builder);

    mh.invoke(3);

    assertEquals(3, builder.length());
  }

  @Test
  void call() throws Throwable {
    final var builder = new StringBuilder("abcde");

    final var callable = linker.link(new SimpleRelinkableCallSite(new CallSiteDescriptor(
        publicLookup(),
        GET.withNamespace(METHOD).named("replace"),
        methodType(Object.class, Object.class)
    ))).dynamicInvoker().bindTo(builder).invoke();

    linker.link(new SimpleRelinkableCallSite(new CallSiteDescriptor(
        publicLookup(),
        CALL,
        methodType(Object.class, Object.class, Object.class, Object.class, Object.class, Object.class)
    ))).dynamicInvoker().invoke(callable, builder, 0, 2, "");

    assertEquals("cde", builder.toString());
  }
}
