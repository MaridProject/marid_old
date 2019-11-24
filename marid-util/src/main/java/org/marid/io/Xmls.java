/*-
 * #%L
 * marid-util
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

package org.marid.io;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.newBufferedReader;

/**
 * @author Dmitry Ovchinnikov
 */
public interface Xmls {

  Consumer<Transformer> FORMATTED_TRANSFORMER_CONFIGURER = t -> {
    t.setOutputProperty(OutputKeys.INDENT, "yes");
    t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
    t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
  };

  static void write(Consumer<DocumentBuilderFactory> documentBuilderFactoryConfigurer,
                    Consumer<DocumentBuilder> documentBuilderConfigurer,
                    Consumer<Document> documentConfigurer,
                    Consumer<TransformerFactory> transformerFactoryConfigurer,
                    Consumer<Transformer> transformerConfigurer,
                    Result result) {
    final var factory = DocumentBuilderFactory.newInstance();
    documentBuilderFactoryConfigurer.accept(factory);
    final Document document;
    try {
      final var builder = factory.newDocumentBuilder();
      documentBuilderConfigurer.accept(builder);
      document = builder.newDocument();
      documentConfigurer.accept(document);
    } catch (ParserConfigurationException e) {
      throw new IllegalStateException(e);
    }
    write(document, transformerFactoryConfigurer, transformerConfigurer, result);
  }

  static void write(Document document,
                    Consumer<TransformerFactory> transformerFactoryConfigurer,
                    Consumer<Transformer> transformerConfigurer,
                    Result result) {
    final var transformerFactory = TransformerFactory.newInstance();
    transformerFactoryConfigurer.accept(transformerFactory);
    try {
      final var transformer = transformerFactory.newTransformer();
      transformerConfigurer.accept(transformer);
      transformer.transform(new DOMSource(document), result);
    } catch (TransformerException e) {
      throw new IllegalStateException(e);
    }
  }

  static <T> T read(Consumer<DocumentBuilderFactory> documentBuilderFactoryConfigurer,
                    Consumer<DocumentBuilder> documentBuilderConfigurer,
                    Function<Document, T> documentReader,
                    InputSource source) {
    final var factory = DocumentBuilderFactory.newInstance();
    documentBuilderFactoryConfigurer.accept(factory);
    try {
      final var builder = factory.newDocumentBuilder();
      documentBuilderConfigurer.accept(builder);
      final var document = builder.parse(source);
      return documentReader.apply(document);
    } catch (ParserConfigurationException | SAXException x) {
      throw new IllegalStateException(x);
    } catch (IOException x) {
      throw new UncheckedIOException(x);
    }
  }

  static void writeFormatted(Document document, Result result) {
    write(document, f -> {}, FORMATTED_TRANSFORMER_CONFIGURER, result);
  }

  static void writeFormatted(Document document, Path file) {
    try (final var writer = Files.newBufferedWriter(file, UTF_8)) {
      writeFormatted(document, new StreamResult(writer));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  static void writeFormatted(Consumer<Document> documentConsumer, Result result) {
    write(f -> {}, b -> {}, documentConsumer, f -> {}, FORMATTED_TRANSFORMER_CONFIGURER, result);
  }

  static void writeFormatted(Consumer<Document> documentConsumer, Path file) {
    try (final var writer = Files.newBufferedWriter(file, UTF_8)) {
      writeFormatted(documentConsumer, new StreamResult(writer));
    } catch (IOException x) {
      throw new UncheckedIOException(x);
    }
  }

  static void writeFormatted(String documentElement, Consumer<Element> elementConsumer, Result result) {
    writeFormatted(d -> {
      final var element = d.createElement(documentElement);
      elementConsumer.accept(element);
      d.appendChild(element);
    }, result);
  }

  static void writeFormatted(String documentElement, Consumer<Element> elementConsumer, Path path) {
    try (final var writer = Files.newBufferedWriter(path, UTF_8)) {
      writeFormatted(documentElement, elementConsumer, new StreamResult(writer));
    } catch (IOException x) {
      throw new UncheckedIOException(x);
    }
  }

  static <T> T read(Function<Document, T> documentReader, Path file) {
    try (final var reader = newBufferedReader(file, UTF_8)) {
      return read(documentReader, reader);
    } catch (IOException x) {
      throw new UncheckedIOException(x);
    }
  }

  static <T> T read(Path file, Function<Element, T> elementReader) {
    return read(d -> elementReader.apply(d.getDocumentElement()), file);
  }

  static <T> T read(Function<Document, T> documentReader, Reader reader) {
    return read(f -> {}, b -> {}, documentReader, new InputSource(reader));
  }

  static <T> T read(Reader reader, Function<Element, T> elementReader) {
    return read(d -> elementReader.apply(d.getDocumentElement()), reader);
  }

  static <E> Stream<E> stream(Class<E> type, Stream<?> stream) {
    return stream.filter(type::isInstance).map(type::cast);
  }

  static <E extends Node> Iterable<E> nodes(Node node, Class<E> type, Predicate<E> filter) {
    return () -> Spliterators.iterator(nodes(node, type).filter(filter).spliterator());
  }

  static <E extends Node> Stream<E> nodes(Node node, Class<E> type) {
    final var children = node.getChildNodes();
    return IntStream.range(0, children.getLength())
        .mapToObj(children::item)
        .filter(type::isInstance)
        .map(type::cast);
  }

  static Stream<Element> elements(Node node) {
    return nodes(node, Element.class);
  }

  static Stream<Element> elements(Node node, String tag) {
    return elements(node).filter(e -> tag.equals(e.getTagName()));
  }

  static Stream<Element> elements(String tag, Node node) {
    return nodes(node, Element.class).filter(e -> tag.equals(e.getTagName())).flatMap(Xmls::elements);
  }

  static Optional<Element> element(String tag, Node node) {
    return elements(tag, node).findFirst();
  }

  static Optional<String> attribute(Element element, String name) {
    return element.hasAttribute(name)
        ? Optional.of(element.getAttribute(name))
        : Optional.empty();
  }

  static Optional<String> content(Element element) {
    return element.hasChildNodes()
        ? Optional.of(element.getTextContent())
        : Optional.empty();
  }

  @SafeVarargs
  static Element create(Node parent, String tag, Consumer<Element>... consumers) {
    final var document = parent instanceof Document ? ((Document) parent) : parent.getOwnerDocument();
    final var element = document.createElement(tag);

    parent.appendChild(element);

    for (final var consumer : consumers) {
      consumer.accept(element);
    }

    return element;
  }
}
