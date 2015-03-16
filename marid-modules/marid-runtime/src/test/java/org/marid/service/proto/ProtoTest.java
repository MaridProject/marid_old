/*
 * Copyright (C) 2015 Dmitry Ovchinnikov
 * Marid, the free data acquisition and visualization software
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.marid.service.proto;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.marid.service.proto.pp.PpService;
import org.marid.service.proto.pp.PpServiceConfiguration;
import org.marid.test.MaridSpringTests;
import org.marid.test.NormalTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.HashSet;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

/**
 * @author Dmitry Ovchinnikov
 */
@Category({NormalTests.class})
@ContextConfiguration(classes = {ProtoTestConfiguration.class})
public class ProtoTest extends MaridSpringTests {

    @Autowired
    private PpServiceConfiguration configuration;

    @Autowired
    private PpService ppService;

    @Test
    public void test() throws Exception {
        while (true) {
            if (ppService.getContext().vars.size() == 4) {
                assertEquals(ppService.getContext().vars.keySet(), new HashSet<>(asList("0", "1", "2", "3")));
                assertEquals(new HashSet<>(ppService.getContext().vars.values()), new HashSet<Object>(asList(10, 20, 30, 40)));
                return;
            }
            Thread.sleep(100L);
        }
    }
}
