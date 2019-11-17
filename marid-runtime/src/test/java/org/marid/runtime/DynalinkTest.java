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
import jdk.dynalink.DynamicLinkerFactory;
import jdk.dynalink.beans.BeansLinker;
import jdk.dynalink.support.SimpleRelinkableCallSite;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.marid.runtime.test.impl.TestBean;

import java.lang.invoke.MethodHandles;

import static java.lang.invoke.MethodType.methodType;
import static jdk.dynalink.StandardNamespace.PROPERTY;
import static jdk.dynalink.StandardOperation.GET;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("normal")
class DynalinkTest {

  @Test
  void test() throws Throwable {
    final var linkerFactory = new DynamicLinkerFactory();
    linkerFactory.setPrioritizedLinker(new BeansLinker());

    final var linker = linkerFactory.createLinker();

    final var lookup = MethodHandles.publicLookup();
    final var op = GET.withNamespace(PROPERTY).named("x");
    final var csd = new CallSiteDescriptor(lookup, op, methodType(Object.class, Object.class));

    final var site = linker.link(new SimpleRelinkableCallSite(csd));
    final var mh = site.dynamicInvoker().bindTo(new TestBean());

    assertEquals("x", mh.invoke());
  }
}
