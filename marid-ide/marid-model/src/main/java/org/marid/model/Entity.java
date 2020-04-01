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

import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.StringReader;
import java.io.StringWriter;

public interface Entity extends Externalizable {

  @Override
  int hashCode();

  @Override
  boolean equals(Object obj);

  @Override
  String toString();

  ModelObjectFactory modelObjectFactory();

  String tag();

  void writeTo(Element element);

  void readFrom(Element element);

  @Override
  default void writeExternal(ObjectOutput out) throws IOException {
    try {
      final var document = DocumentBuilderFactory.newDefaultInstance().newDocumentBuilder().newDocument();
      final var element = document.createElement(tag());
      document.appendChild(element);
      writeTo(element);
      final var transformerFactory = TransformerFactory.newDefaultInstance();
      final var transformer = transformerFactory.newTransformer();
      final var stringWriter = new StringWriter();
      transformer.transform(new DOMSource(document), new StreamResult(stringWriter));
      out.writeUTF(stringWriter.toString());
    } catch (ParserConfigurationException | TransformerException e) {
      throw new IOException(e);
    }
  }

  @Override
  default void readExternal(ObjectInput in) throws IOException {
    try {
      final var documentBuilder = DocumentBuilderFactory.newDefaultInstance().newDocumentBuilder();
      final var document = documentBuilder.parse(new InputSource(new StringReader(in.readUTF())));
      final var element = document.getDocumentElement();
      if (!element.getTagName().equals(tag())) {
        throw new IllegalArgumentException("Tag mismatch: " + element.getTagName() + " != " + tag());
      }
      readFrom(element);
    } catch (ParserConfigurationException | SAXException e) {
      throw new IOException(e);
    }
  }
}
