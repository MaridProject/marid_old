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

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.marid.misc.Builder;
import org.xml.sax.InputSource;

import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("normal")
class SerializationTest {

  @Test
  void testRack() throws IOException {
    final var winery = Builder.build(new Winery("winery1"));
    final var cellar1 = Builder.build(new Cellar("cellar1"));
    final var cellar2 = Builder.build(new Cellar("cellar2"));
    final var rack11 = Builder.build(new Rack("rack11", List.of(), List.of()));
    final var rack21 = Builder.build(new Rack("rack21", List.of(), List.of()));
    final var rack22 = Builder.build(new Rack("rack22", List.of(), List.of()));

    winery.getCellars().addAll(Arrays.asList(cellar1, cellar2));
    cellar1.getRacks().addAll(Collections.singletonList(rack11));
    cellar2.getRacks().addAll(Arrays.asList(rack21, rack22));

    final var writer = new StringWriter();
    {
      winery.save(new StreamResult(writer));
      final var deserialized = new Winery(new InputSource(new StringReader(writer.toString())));
      assertEquals(winery, deserialized);
    }
  }
}
