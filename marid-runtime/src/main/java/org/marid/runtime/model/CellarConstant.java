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
import java.util.Objects;
import java.util.stream.Collectors;

public final class CellarConstant extends AbstractEntity {

  private String lib;
  private String selector;
  private String name;
  private final ArrayList<ConstantArgument> arguments;

  public CellarConstant(@NotNull String lib, @NotNull String selector, @NotNull String name) {
    this.lib = lib;
    this.name = name;
    this.arguments = new ArrayList<>();
  }

  public CellarConstant(@NotNull Element element) {
    super(element);
    this.lib = element.getAttribute("lib");
    this.selector = element.getAttribute("selector");
    this.name = element.getAttribute("name");
    this.arguments = XmlStreams.children(element, Element.class)
        .map(ConstantArgument::argument)
        .collect(Collectors.toCollection(ArrayList::new));
  }

  public CellarConstant(@NotNull InputSource source) {
    this(element(source));
  }

  @NotNull
  @Override
  public String getTag() {
    return "const";
  }

  @Override
  public void writeTo(@NotNull Element element) {
    element.setAttribute("lib", lib);
    element.setAttribute("selector", selector);
    element.setAttribute("name", name);
    arguments.forEach(e -> XmlUtils.appendTo(e, element));
  }

  @NotNull
  public CellarConstant addArg(ArgumentLiteral arg) {
    arguments.add(arg);
    return this;
  }

  @NotNull
  public ArrayList<ConstantArgument> getArguments() {
    return arguments;
  }

  public String getName() {
    return name;
  }

  @NotNull
  public CellarConstant setName(String name) {
    this.name = name;
    return this;
  }

  @NotNull
  public String getLib() {
    return lib;
  }

  @NotNull
  public CellarConstant setLib(String lib) {
    this.lib = lib;
    return this;
  }

  @NotNull
  public String getSelector() {
    return selector;
  }

  @NotNull
  public CellarConstant setSelector(String selector) {
    this.selector = selector;
    return this;
  }

  @Override
  public int hashCode() {
    return Objects.hash(lib, selector, name, arguments);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof CellarConstant) {
      final var that = (CellarConstant) obj;
      return Objects.equals(this.lib, that.lib)
          && Objects.equals(this.selector, that.selector)
          && Objects.equals(this.name, that.name)
          && Objects.equals(this.arguments, that.arguments);
    }
    return false;
  }
}
