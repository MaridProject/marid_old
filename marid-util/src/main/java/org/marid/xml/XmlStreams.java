package org.marid.xml;

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

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public interface XmlStreams {

  static Stream<Element> elementsByTag(Element node, String tag) {
    final var nodeList = node.getElementsByTagName(tag);
    return IntStream.range(0, nodeList.getLength())
        .mapToObj(nodeList::item)
        .filter(Element.class::isInstance)
        .map(Element.class::cast);
  }

  static Stream<Node> children(Node node) {
    final var nodeList = node.getChildNodes();
    return IntStream.range(0, nodeList.getLength())
        .mapToObj(nodeList::item);
  }

  static <N extends Node> Stream<N> children(Node node, Class<N> nodeType) {
    return children(node)
        .filter(nodeType::isInstance)
        .map(nodeType::cast);
  }

  static Stream<Node> parents(Node node) {
    final var parent = node.getParentNode();
    return parent == null ? Stream.of(node) : Stream.concat(Stream.of(node), parents(parent));
  }

  static Stream<Node> nextSiblings(Node node) {
    final var next = node.getNextSibling();
    return next == null ? Stream.of(node) : Stream.concat(Stream.of(node), nextSiblings(next));
  }

  static <N extends Node> Stream<N> nextSiblings(Node node, Class<N> nodeType) {
    return nextSiblings(node)
        .filter(nodeType::isInstance)
        .map(nodeType::cast);
  }

  static Stream<Node> prevSiblings(Node node) {
    final var prev = node.getPreviousSibling();
    return prev == null ? Stream.of(node) : Stream.concat(Stream.of(node), prevSiblings(prev));
  }

  static <N extends Node> Stream<N> prevSiblings(Node node, Class<N> nodeType) {
    return prevSiblings(node)
        .filter(nodeType::isInstance)
        .map(nodeType::cast);
  }
}
