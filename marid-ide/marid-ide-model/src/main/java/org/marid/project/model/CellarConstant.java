package org.marid.project.model;

/*-
 * #%L
 * marid-ide-model
 * %%
 * Copyright (C) 2012 - 2019 MARID software development group
 * %%
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 * 
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
  private String name;
  private final ArrayList<ArgumentLiteral> arguments;

  public CellarConstant(@NotNull String lib, @NotNull String name) {
    this.lib = lib;
    this.name = name;
    this.arguments = new ArrayList<>();
  }

  public CellarConstant(@NotNull Element element) {
    super(element);
    this.lib = element.getAttribute("lib");
    this.name = element.getAttribute("name");
    this.arguments = XmlStreams.children(element, Element.class)
        .map(ArgumentLiteral::new)
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
    element.setAttribute("name", name);
    arguments.forEach(e -> XmlUtils.appendTo(e, element));
  }

  public CellarConstant addArg(ArgumentLiteral arg) {
    arguments.add(arg);
    return this;
  }

  public ArrayList<ArgumentLiteral> getArguments() {
    return arguments;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getLib() {
    return lib;
  }

  public void setLib(String lib) {
    this.lib = lib;
  }

  @Override
  public int hashCode() {
    return Objects.hash(lib, name, arguments);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof CellarConstant) {
      final var that = (CellarConstant) obj;
      return Objects.equals(this.lib, that.lib)
          && Objects.equals(this.name, that.name)
          && Objects.equals(this.arguments, that.arguments);
    }
    return false;
  }
}
