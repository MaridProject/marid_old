package org.marid.racks.collection;

/*-
 * #%L
 * marid-racks
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

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.marid.runtime.AbstractCellar;
import org.marid.runtime.util.DeploymentBuilder;

import java.util.Objects;

@Tag("normal")
class ArrayListRackTest {

  @Test
  void testInference() throws Exception {
    try (final var deployment = new DeploymentBuilder("test")
        .addCellar(TestCellar.class)
        .build()) {
      deployment.start();
    }
  }

  public static class TestCellar extends AbstractCellar {

    private static TestCellar provider;

    public static synchronized TestCellar provider() {
      return Objects.requireNonNullElseGet(provider, () -> provider = new TestCellar());
    }
  }
}
