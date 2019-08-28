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

public final class RackInit extends AbstractEntity {

  private String cellar;
  private String rack;
  private String method;
  private final ArrayList<Argument> arguments;

  public RackInit(@NotNull String cellar,
                  @NotNull String rack,
                  @NotNull String method) {
    this.cellar = cellar;
    this.rack = rack;
    this.method = method;
    this.arguments = new ArrayList<>();
  }

  public RackInit(@NotNull Element element) {
    this.cellar = element.getAttribute("cellar");
    this.rack = element.getAttribute("rack");
    this.method = element.getAttribute("method");
    this.arguments = XmlStreams.children(element, Element.class)
        .map(ArgumentFactory::argument)
        .collect(Collectors.toCollection(ArrayList::new));
  }

  public RackInit(@NotNull InputSource inputSource) {
    this(element(inputSource));
  }

  public RackInit addArg(Argument argument) {
    arguments.add(argument);
    return this;
  }

  @Override
  public @NotNull String getTag() {
    return "init";
  }

  @Override
  public void writeTo(@NotNull Element element) {
    element.setAttribute("cellar", cellar);
    element.setAttribute("rack", rack);
    element.setAttribute("method", method);
    arguments.forEach(a -> XmlUtils.appendTo(a, element));
  }

  @Override
  public int hashCode() {
    return Objects.hash(cellar, rack, method, arguments);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof RackInit) {
      final var that = (RackInit) obj;
      return Objects.equals(this.cellar, that.cellar)
          && Objects.equals(this.rack, that.rack)
          && Objects.equals(this.method, that.method)
          && Objects.equals(this.arguments, that.arguments);
    }
    return false;
  }
}
