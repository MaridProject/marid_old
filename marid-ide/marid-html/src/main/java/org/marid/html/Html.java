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

import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import java.util.function.Consumer;

public final class Html extends HtmlElement implements HtmlBase<Html> {

  public Html(Document document) {
    super(document.createElement("html"));
    document.appendChild(getNode());
  }

  public Html() {
    this(createDocument());
  }

  public Html head(Consumer<Head> headConsumer) {
    final var head = new Head(this);
    headConsumer.accept(head);
    return this;
  }

  public Html body(Consumer<Body> bodyConsumer) {
    final var body = new Body(this);
    bodyConsumer.accept(body);
    return this;
  }

  @Override
  protected DOMSource domSource() {
    return new DOMSource(getNode().getOwnerDocument());
  }

  @Override
  protected void initTransformer(Transformer transformer) {
    super.initTransformer(transformer);
    transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "about:legacy-compat");
    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
  }

  @Override
  public Html getSelf() {
    return this;
  }

  public static Document createDocument() {
    final var documentBuilderFactory = DocumentBuilderFactory.newDefaultInstance();

    try {
      final var documentBuilder = documentBuilderFactory.newDocumentBuilder();
      return documentBuilder.newDocument();
    } catch (ParserConfigurationException e) {
      throw new IllegalStateException(e);
    }
  }
}
