package org.marid.project.model;

/*-
 * #%L
 * marid-ide-model
 * %%
 * Copyright (C) 2012 - 2019 MARID software development group
 * %%
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 * 
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 * #L%
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.marid.misc.Builder;
import org.marid.project.json.JsonMapperContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("normal")
@ExtendWith({SpringExtension.class})
@ContextConfiguration(classes = {JsonMapperContext.class})
class JsonSerializationTest {

  @Autowired
  private ObjectMapper mapper;

  @Test
  void testRack() throws IOException {
    final var winery = Builder.build(new Winery(),
        c -> c.id = "w1",
        c -> c.name = "winery1"
    );

    final var cellar1 = Builder.build(new Cellar(winery),
        c -> c.id = "c1",
        c -> c.name = "cellar1"
    );

    final var cellar2 = Builder.build(new Cellar(winery),
        c -> c.id = "c2",
        c -> c.name = "cellar2"
    );

    final var rack11 = Builder.build(new Rack(cellar1),
        c -> c.id = "r11",
        c -> c.name = "rack11"
    );

    final var rack21 = Builder.build(new Rack(cellar2),
        c -> c.id = "r21",
        c -> c.name = "rack21"
    );

    final var rack22 = Builder.build(new Rack(cellar2),
        c -> c.id = "r22",
        c -> c.name = "rack22"
    );

    {
      final var serialized = mapper.writeValueAsString(winery);
      final var deserialized = mapper.readValue(serialized, Winery.class);
      assertEquals(winery, deserialized);
    }

    {
      final var serialized = mapper.writeValueAsString(cellar2);
      final var deserialized = mapper.readValue(serialized, Cellar.class);
      assertEquals(cellar2, deserialized);
    }

    {
      final var serialized = mapper.writeValueAsString(rack11);
      final var deserialized = mapper.readValue(serialized, Rack.class);
      assertEquals(rack11, deserialized);
    }
  }
}
