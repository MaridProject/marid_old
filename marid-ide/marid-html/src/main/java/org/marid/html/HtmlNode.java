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

import org.w3c.dom.Node;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import java.io.IOException;

public abstract class HtmlNode<N extends Node> implements HasNode<N> {

  private final N node;

  protected HtmlNode(N node) {
    this.node = node;
  }

  @Override
  public N getNode() {
    return node;
  }

  public void write(Result result) throws IOException {
    final var transformerFactory = TransformerFactory.newDefaultInstance();
    initTransformerFactory(transformerFactory);

    final Transformer transformer;
    try {
      transformer = transformerFactory.newTransformer();
      initTransformer(transformer);
    } catch (TransformerConfigurationException e) {
      throw new IOException(e);
    }

    try {
      transformer.transform(domSource(), result);
    } catch (TransformerException e) {
      if (e.getCause() instanceof IOException) {
        throw (IOException) e.getCause();
      } else {
        throw new IOException(e);
      }
    }
  }

  protected DOMSource domSource() {
    return new DOMSource(node);
  }

  protected void initTransformerFactory(TransformerFactory transformerFactory) {
    transformerFactory.setAttribute("indent-number", 2);
  }

  protected void initTransformer(Transformer transformer) {
    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.setOutputProperty(OutputKeys.METHOD, "html");
    transformer.setOutputProperty(OutputKeys.VERSION, "5");
  }
}
