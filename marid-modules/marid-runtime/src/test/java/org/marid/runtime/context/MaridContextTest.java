/*-
 * #%L
 * marid-runtime
 * %%
 * Copyright (C) 2012 - 2017 MARID software development group
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

package org.marid.runtime.context;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.marid.runtime.beans.Bean;
import org.marid.runtime.beans.BeanProducer;
import org.marid.test.NormalTests;

import java.math.BigDecimal;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.marid.runtime.beans.Bean.signature;
import static org.marid.runtime.context.MaridContextTestUtils.m;

/**
 * @author Dmitry Ovchinnikov
 */
@Category({NormalTests.class})
public class MaridContextTest {

    @Test
    public void simple() throws Exception {
        final Bean[] beans = {
                new Bean(
                        "bean2",
                        "@bean1",
                        new BeanProducer(signature(Bean1.class.getMethod("getZ")))
                ),
                new Bean(
                        "bean1",
                        Bean1.class.getName(),
                        new BeanProducer(
                                signature(Bean1.class.getConstructor(int.class, String.class, BigDecimal.class)),
                                m("x", "int", "1"),
                                m("y", "String", "abc"),
                                m("z", "BigDecimal", "1.23")
                        ),
                        new BeanProducer(
                                signature(Bean1.class.getMethod("setA", boolean.class)),
                                m("a", "boolean", "true")
                        )
                ),
                new Bean(
                        "bean3",
                        "@bean1",
                        new BeanProducer(signature(Bean1.class.getField("y")))
                ),
                new Bean(
                        "bean4",
                        Bean1.class.getName(),
                        new BeanProducer(signature(Bean1.class.getMethod("list"))),
                        new BeanProducer(
                                signature(List.class.getMethod("add", Object.class)),
                                m("e", "int", "1")
                        )
                ),
                new Bean(
                        "bean5",
                        Bean1.class.getName(),
                        new BeanProducer(signature(Bean1.class.getMethod("list"))),
                        new BeanProducer(
                                signature(List.class.getMethod("add", Object.class)),
                                m("e", "int", "1")
                        ),
                        new BeanProducer(
                                signature(List.class.getMethod("add", Object.class)),
                                m("e", "String", "length", "22")
                        )
                ),
                new Bean(
                        "bean6",
                        String.class.getName(),
                        new BeanProducer(
                                signature(String.class.getMethod("valueOf", Object.class)),
                                m("arg0", "js", "'a' + 1")
                        )
                )
        };
        try (final MaridContext runtime = new MaridContext(new MaridConfiguration(beans))) {
            assertEquals(new Bean1(1, "abc", new BigDecimal("1.23")).setA(true), runtime.beans.get("bean1"));
            assertEquals(new BigDecimal("1.23"), runtime.beans.get("bean2"));
            assertEquals("abc", runtime.beans.get("bean3"));
            assertEquals(singletonList(1), runtime.beans.get("bean4"));
            assertEquals(asList(1, 2), runtime.beans.get("bean5"));
            assertEquals("a1", runtime.beans.get("bean6"));
            assertEquals(10, ((Bean1) runtime.beans.get("bean1")).q);
        }
    }

    @Test(expected = MaridContextException.class)
    public void circularReferenceDetection() throws Exception {
        final Bean[] beans = {
                new Bean(
                        "bean1",
                        Bean1.class.getName(),
                        new BeanProducer(
                                signature(Bean1.class.getConstructor(int.class, String.class, BigDecimal.class)),
                                m("x", "int", "1"), m("y", "ref", "toString", "bean1"), m("BigDecimal", "z", "1.23")
                        ),
                        new BeanProducer(
                                signature(Bean1.class.getMethod("setA", boolean.class)),
                                m("a", "boolean", "true")
                        )
                )
        };
        try (final MaridContext context = new MaridContext(new MaridConfiguration(beans))) {
            assertNull(context);
        } catch (MaridContextException x) {
            assertEquals("Bean circular reference detected: bean1 [bean1]", x.getSuppressed()[0].getMessage());
            throw x;
        }
    }
}