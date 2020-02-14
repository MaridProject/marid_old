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

import java.lang.reflect.Executable;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

public final class CellarConstantImpl extends AbstractEntity {

  private String factory;
  private String selector;
  private String name;
  private final ArrayList<AbstractConstant> arguments;

  public CellarConstantImpl(@NotNull String factory, @NotNull String selector, @NotNull String name) {
    this.factory = factory;
    this.selector = selector;
    this.name = name;
    this.arguments = new ArrayList<>();
  }

  public CellarConstantImpl(@NotNull Executable executable, @NotNull String name) {
    this(executable.getDeclaringClass().getName(), executable.getName(), name);
  }

  public CellarConstantImpl(@NotNull Element element) {
    super(element);
    this.factory = element.getAttribute("factory");
    this.selector = element.getAttribute("selector");
    this.name = element.getAttribute("name");
    this.arguments = XmlStreams.children(element, Element.class)
        .map(AbstractConstant::argument)
        .collect(Collectors.toCollection(ArrayList::new));
  }

  public CellarConstantImpl(@NotNull InputSource source) {
    this(element(source));
  }

  @NotNull
  @Override
  public String getTag() {
    return "const";
  }

  @Override
  public void writeTo(@NotNull Element element) {
    element.setAttribute("factory", factory);
    element.setAttribute("selector", selector);
    element.setAttribute("name", name);
    arguments.forEach(e -> XmlUtils.appendTo(e, element));
  }

  @NotNull
  public CellarConstantImpl addArg(AbstractConstant arg) {
    arguments.add(arg);
    return this;
  }

  @NotNull
  public ArrayList<AbstractConstant> getArguments() {
    return arguments;
  }

  public String getName() {
    return name;
  }

  @NotNull
  public CellarConstantImpl setName(String name) {
    this.name = name;
    return this;
  }

  @NotNull
  public String getFactory() {
    return factory;
  }

  @NotNull
  public CellarConstantImpl setFactory(String factory) {
    this.factory = factory;
    return this;
  }

  @NotNull
  public String getSelector() {
    return selector;
  }

  @NotNull
  public CellarConstantImpl setSelector(String selector) {
    this.selector = selector;
    return this;
  }

  @Override
  public int hashCode() {
    return Objects.hash(factory, selector, name, arguments);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof CellarConstantImpl) {
      final var that = (CellarConstantImpl) obj;
      return Objects.equals(this.factory, that.factory)
          && Objects.equals(this.selector, that.selector)
          && Objects.equals(this.name, that.name)
          && Objects.equals(this.arguments, that.arguments);
    }
    return false;
  }
}
