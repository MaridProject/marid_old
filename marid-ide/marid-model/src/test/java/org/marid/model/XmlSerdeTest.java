package org.marid.model;

/*-
 * #%L
 * marid-model
 * %%
 * Copyright (C) 2012 - 2020 MARID software development group
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
import org.marid.misc.Builder;

import javax.xml.parsers.DocumentBuilderFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("normal")
class XmlSerdeTest {

  @Test
  void cellarConstant() throws Exception {
    final var expected = new CellarConstantImpl("f1", "s1", "n1");
    expected.addArgument(NullImpl.INSTANCE);
    expected.addArgument(new LiteralImpl(Literal.Type.BASE64, "x"));
    final var document = DocumentBuilderFactory.newDefaultInstance().newDocumentBuilder().newDocument();
    final var element = document.createElement("test");
    expected.writeTo(element);
    final var actual = ModelObjectFactory.FACTORY.newCellarConstant();
    actual.readFrom(element);
    assertEquals(expected, actual);
  }

  @Test
  void rack() throws Exception {
    final var expected = new RackImpl("r1", "f1");
    expected.addArgument(new LiteralImpl(Literal.Type.METHODTYPE, "x"));
    expected.addInitializer(new InitializerImpl("x", NullImpl.INSTANCE, new LiteralImpl(Literal.Type.BYTE, "1")));
    final var document = DocumentBuilderFactory.newDefaultInstance().newDocumentBuilder().newDocument();
    final var element = document.createElement("test");
    expected.writeTo(element);
    final var actual = ModelObjectFactory.FACTORY.newRack();
    actual.readFrom(element);
    assertEquals(expected, actual);
  }

  @Test
  void cellar() throws Exception {
    final var expected = new CellarImpl("c1");
    expected.addConstant(Builder.build(new CellarConstantImpl("f1", "s1", "n1"), c -> {
      c.addArgument(NullImpl.INSTANCE);
      c.addArgument(new LiteralImpl(Literal.Type.BASE64, "x"));
    }));
    expected.addRack(Builder.build(new RackImpl("r1", "f1"), r -> {
      r.addArgument(new LiteralImpl(Literal.Type.METHODTYPE, "x"));
      r.addInitializer(new InitializerImpl("x", NullImpl.INSTANCE, new LiteralImpl(Literal.Type.BYTE, "1")));
    }));
    final var document = DocumentBuilderFactory.newDefaultInstance().newDocumentBuilder().newDocument();
    final var element = document.createElement("test");
    expected.writeTo(element);
    final var actual = ModelObjectFactory.FACTORY.newCellar();
    actual.readFrom(element);
    assertEquals(expected, actual);
  }

  @Test
  void winery() throws Exception {
    final var expected = new WineryImpl("g1", "n1", "v1");
    expected.addCellar(Builder.build(new CellarImpl("c1"), ce -> {
      ce.addConstant(Builder.build(new CellarConstantImpl("f1", "s1", "n1"), c -> {
        c.addArgument(NullImpl.INSTANCE);
        c.addArgument(new LiteralImpl(Literal.Type.BASE64, "x"));
      }));
      ce.addRack(Builder.build(new RackImpl("r1", "f1"), r -> {
        r.addArgument(new LiteralImpl(Literal.Type.METHODTYPE, "x"));
        r.addInitializer(new InitializerImpl("x", NullImpl.INSTANCE, new LiteralImpl(Literal.Type.BYTE, "1")));
      }));
    }));
    final var document = DocumentBuilderFactory.newDefaultInstance().newDocumentBuilder().newDocument();
    final var element = document.createElement("test");
    expected.writeTo(element);
    final var actual = ModelObjectFactory.FACTORY.newWinery();
    actual.readFrom(element);
    assertEquals(expected, actual);
  }
}
