package org.marid.runtime.model;

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

import org.jetbrains.annotations.NotNull;
import org.marid.xml.Tagged;
import org.marid.xml.XmlWritable;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;

abstract class AbstractEntity implements Tagged, XmlWritable {

  AbstractEntity() {
  }

  AbstractEntity(@NotNull Element element) {
    if (!getTag().equals(element.getTagName())) {
      throw new IllegalArgumentException(element.getTagName());
    }
  }

  public void save(Result result) {
    try {
      final var documentBuilderFactory = DocumentBuilderFactory.newDefaultInstance();
      final var documentBuilder = documentBuilderFactory.newDocumentBuilder();
      final var document = documentBuilder.newDocument();
      final var node = document.createElement(getTag());
      document.appendChild(node);
      writeTo(node);
      final var transformerFactory = TransformerFactory.newDefaultInstance();
      transformerFactory.setAttribute("indent-number", 2);
      final var transformer = transformerFactory.newTransformer();
      transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty(OutputKeys.STANDALONE, "no");
      transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
      transformer.transform(new DOMSource(node), result);
    } catch (TransformerException | ParserConfigurationException e) {
      throw new IllegalStateException(e);
    }
  }

  static Element element(InputSource inputSource) {
    try {
      final var documentBuilderFactory = DocumentBuilderFactory.newDefaultInstance();
      final var documentBuilder = documentBuilderFactory.newDocumentBuilder();
      final var document = documentBuilder.parse(inputSource);
      return document.getDocumentElement();
    } catch (ParserConfigurationException | SAXException e) {
      throw new IllegalStateException(e);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public abstract int hashCode();

  @Override
  public abstract boolean equals(Object obj);

  @Override
  public String toString() {
    final var writer = new StringWriter(1024);
    save(new StreamResult(writer));
    return writer.toString();
  }
}
