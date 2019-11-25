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

public final class Rack extends AbstractEntity {

  private final ArrayList<Argument> arguments;
  private final ArrayList<Input> inputs;
  private final ArrayList<Initializer> initializers;
  private final ArrayList<Destroyer> destroyers;

  private String name;
  private String factory;

  public Rack(@NotNull String name, @NotNull String factory) {
    this.name = name;
    this.factory = factory;
    this.arguments = new ArrayList<>();
    this.inputs = new ArrayList<>();
    this.initializers = new ArrayList<>();
    this.destroyers = new ArrayList<>();
  }

  public Rack(@NotNull Element element) {
    super(element);
    this.name = element.getAttribute("name");
    this.factory = element.getAttribute("factory");
    this.arguments = XmlStreams.elementsByTag(element, "args")
        .flatMap(e -> XmlStreams.children(e, Element.class).map(Argument::argument))
        .collect(Collectors.toCollection(ArrayList::new));
    this.inputs = XmlStreams.elementsByTag(element, "inputs")
        .flatMap(e -> XmlStreams.children(e, Element.class).map(Input::new))
        .collect(Collectors.toCollection(ArrayList::new));
    this.initializers = XmlStreams.elementsByTag(element, "initializers")
        .flatMap(e -> XmlStreams.children(e, Element.class).map(Initializer::new))
        .collect(Collectors.toCollection(ArrayList::new));
    this.destroyers = XmlStreams.elementsByTag(element, "destroyers")
        .flatMap(e -> XmlStreams.children(e, Element.class).map(Destroyer::new))
        .collect(Collectors.toCollection(ArrayList::new));
  }

  public Rack(@NotNull InputSource source) {
    this(element(source));
  }

  public String getName() {
    return name;
  }

  public Rack setName(String name) {
    this.name = name;
    return this;
  }

  public String getFactory() {
    return factory;
  }

  public Rack setFactory(String factory) {
    this.factory = factory;
    return this;
  }

  public Rack addArguments(Argument... arguments) {
    this.arguments.addAll(Arrays.asList(arguments));
    return this;
  }

  public Rack addInputs(Input... inputs) {
    this.inputs.addAll(Arrays.asList(inputs));
    return this;
  }

  public Rack addInitializers(Initializer... initializers) {
    this.initializers.addAll(Arrays.asList(initializers));
    return this;
  }

  public Rack addDestroyers(Destroyer... destroyers) {
    this.destroyers.addAll(Arrays.asList(destroyers));
    return this;
  }

  public ArrayList<Argument> getArguments() {
    return arguments;
  }

  @Override
  public void writeTo(@NotNull Element element) {
    element.setAttribute("name", name);
    element.setAttribute("factory", factory);
    if (!arguments.isEmpty()) {
      XmlUtils.append(element, "args", e -> arguments.forEach(a -> XmlUtils.appendTo(a, e)));
    }
  }

  @NotNull
  @Override
  public String getTag() {
    return "rack";
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, factory, arguments, inputs, initializers, destroyers);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof Rack) {
      final var that = (Rack) obj;
      return Objects.equals(this.name, that.name)
          && Objects.equals(this.factory, that.factory)
          && Objects.equals(this.arguments, that.arguments)
          && Objects.equals(this.inputs, that.inputs)
          && Objects.equals(this.initializers, that.initializers)
          && Objects.equals(this.destroyers, that.destroyers);
    }
    return false;
  }
}
