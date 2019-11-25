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
import org.marid.xml.XmlStreams;
import org.marid.xml.XmlUtils;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class Method<M extends Method<M>> extends AbstractEntity {

  private String name;
  private final ArrayList<Argument> arguments;

  Method(@NotNull String name) {
    this.name = name;
    this.arguments = new ArrayList<>();
  }

  Method(@NotNull Element element) {
    super(element);
    this.name = element.getAttribute("name");
    this.arguments = XmlStreams.elementsByTag(element, "args")
        .flatMap(e -> XmlStreams.children(e, Element.class).map(Argument::argument))
        .collect(Collectors.toCollection(ArrayList::new));
  }

  Method(@NotNull InputSource inputSource) {
    this(element(inputSource));
  }

  public String getName() {
    return name;
  }

  @SuppressWarnings("unchecked")
  public M setName(String name) {
    this.name = name;
    return (M) this;
  }

  @SuppressWarnings("unchecked")
  public M addArguments(Argument... arguments) {
    this.arguments.addAll(Arrays.asList(arguments));
    return (M) this;
  }

  public ArrayList<Argument> getArguments() {
    return arguments;
  }

  @Override
  public void writeTo(@NotNull Element element) {
    element.setAttribute("name", name);
    if (!arguments.isEmpty()) {
      XmlUtils.append(element, "args", e -> arguments.forEach(a -> XmlUtils.appendTo(a, e)));
    }
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, arguments);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof Method) {
      final var that = (Method<?>) obj;
      return this.getClass() == that.getClass()
          && Objects.equals(this.name, that.name)
          && Objects.equals(this.arguments, that.arguments);
    }
    return false;
  }
}
