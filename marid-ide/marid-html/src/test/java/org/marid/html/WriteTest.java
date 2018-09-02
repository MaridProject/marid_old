package org.marid.html;

/*-
 * #%L
 * marid-html
 * %%
 * Copyright (C) 2012 - 2018 MARID software development group
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

import org.junit.jupiter.api.Test;

import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.marid.test.io.Resources.loadString;

class WriteTest {

  @Test
  void testWriteRoot() throws IOException {
    final var root = new Html();

    final var stringWriter = new StringWriter();
    root.write(new StreamResult(stringWriter));

    assertEquals(loadString(getClass().getResource("testWriteRoot.html")), stringWriter.toString().trim());
  }

  @Test
  void testWriteHead() throws IOException {
    final var root = new Html();
    new Head(root);

    final var stringWriter = new StringWriter();
    root.write(new StreamResult(stringWriter));

    assertEquals(loadString(getClass().getResource("testWriteHead.html")), stringWriter.toString().trim());
  }

  @Test
  void testWriteFragment() throws IOException {
    final var fragment = new Fragment();
    new Div(fragment);

    final var stringWriter = new StringWriter();
    fragment.write(new StreamResult(stringWriter));

    assertEquals("<div></div>", stringWriter.toString().trim());
  }
}
