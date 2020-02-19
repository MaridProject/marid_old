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

import java.lang.invoke.MethodHandles;
import java.util.Locale;
import java.util.TimeZone;

import static java.lang.invoke.MethodType.methodType;
import static jdk.dynalink.StandardNamespace.METHOD;
import static jdk.dynalink.StandardNamespace.PROPERTY;
import static jdk.dynalink.StandardOperation.CALL;
import static jdk.dynalink.StandardOperation.GET;
import static jdk.dynalink.StandardOperation.NEW;
import static jdk.dynalink.StandardOperation.SET;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
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
    final var actual = linker.link(new SimpleRelinkableCallSite(new CallSiteDescriptor(
      MethodHandles.publicLookup(),
      GET.withNamespace(PROPERTY).named("x"),
      methodType(Object.class, Object.class)))
    ).dynamicInvoker().bindTo(new TestBean()).invoke();

    assertEquals("x", actual);
  }

  @Test
  void getStatic() throws Throwable {
    final var actual = linker.link(new SimpleRelinkableCallSite(new CallSiteDescriptor(
      MethodHandles.publicLookup(),
      GET.withNamespace(PROPERTY).named("LONG"),
      methodType(Object.class, StaticClass.class)))
    ).dynamicInvoker().bindTo(StaticClass.forClass(TimeZone.class)).invoke();

    assertEquals(TimeZone.LONG, actual);
  }

  @Test
  void constructor() throws Throwable {
    final var actual = linker.link(new SimpleRelinkableCallSite(new CallSiteDescriptor(
      MethodHandles.publicLookup(),
      NEW.named("new"),
      methodType(Object.class, Object.class)))
    ).dynamicInvoker().invoke(StaticClass.forClass(TestBean.class));

    assertTrue(actual instanceof TestBean);
  }

  @Test
  void setter() throws Throwable {
    final var builder = new StringBuilder();

    linker.link(new SimpleRelinkableCallSite(new CallSiteDescriptor(
      MethodHandles.publicLookup(),
      SET.withNamespace(PROPERTY).named("length"),
      methodType(void.class, Object.class, Object.class)))
    ).dynamicInvoker().bindTo(builder).invoke(3);

    assertEquals(3, builder.length());
  }

  @Test
  void setterVararg() throws Throwable {
    final var testBean = new TestBean();

    linker.link(new SimpleRelinkableCallSite(new CallSiteDescriptor(
      MethodHandles.publicLookup(),
      SET.withNamespace(PROPERTY).named("values"),
      methodType(void.class, Object.class, Object.class)))
    ).dynamicInvoker().bindTo(testBean).invoke((Object) new String[]{"a", "b"});

    assertArrayEquals(new String[]{"a", "b"}, testBean.getValues());
  }

  @Test
  void call() throws Throwable {
    final var builder = new StringBuilder("abcde");

    final var callable = linker.link(new SimpleRelinkableCallSite(new CallSiteDescriptor(
      MethodHandles.publicLookup(),
      GET.withNamespace(METHOD).named(StringBuilder.class.getMethod("replace", int.class, int.class, String.class).getName()),
      methodType(Object.class, Object.class)
    ))).dynamicInvoker().bindTo(builder).invoke();

    linker.link(new SimpleRelinkableCallSite(new CallSiteDescriptor(
      MethodHandles.publicLookup(),
      CALL,
      methodType(Object.class, Object.class, Object.class, Object.class, Object.class, Object.class)
    ))).dynamicInvoker().invoke(callable, builder, 0, 2, "");

    assertEquals("cde", builder.toString());
  }

  @Test
  void callStatic() throws Throwable {
    final var callable = linker.link(new SimpleRelinkableCallSite(new CallSiteDescriptor(
      MethodHandles.publicLookup(),
      GET.withNamespace(METHOD).named(Locale.class.getMethod("forLanguageTag", String.class).getName()),
      methodType(Object.class, StaticClass.class)
    ))).dynamicInvoker().bindTo(StaticClass.forClass(Locale.class)).invoke();

    final var actual = linker.link(new SimpleRelinkableCallSite(new CallSiteDescriptor(
      MethodHandles.publicLookup(),
      CALL,
      methodType(Object.class, Object.class, Object.class, Object.class)
    ))).dynamicInvoker().invoke(callable, null, "es");

    assertEquals(Locale.forLanguageTag("es"), actual);
  }

  @Test
  void zero() throws Throwable {
    assertEquals(0, MethodHandles.zero(int.class).invoke());
    assertEquals(0L, MethodHandles.zero(long.class).invoke());
    assertEquals(false, MethodHandles.zero(boolean.class).invoke());
  }
}


