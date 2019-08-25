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

public final class Cellar extends AbstractEntity {

  private final ArrayList<Rack> racks;

  private String name;

  public Cellar(@NotNull String name) {
    this.name = name;
    this.racks = new ArrayList<>();
  }

  public Cellar(@NotNull Element element) {
    super(element);
    this.name = element.getAttribute("name");
    this.racks = XmlStreams.elementsByTag(element, "rack")
        .map(Rack::new)
        .collect(Collectors.toCollection(ArrayList::new));
  }

  public Cellar(@NotNull InputSource source) {
    this(element(source));
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ArrayList<Rack> getRacks() {
    return racks;
  }

  @Override
  public void writeTo(@NotNull Element element) {
    element.setAttribute("name", name);
    racks.forEach(r -> XmlUtils.appendTo(r, element));
  }

  @NotNull
  @Override
  public String getTag() {
    return "cellar";
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, racks);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof Cellar) {
      final var that = (Cellar) obj;
      return Objects.equals(this.name, that.name)
          && Objects.equals(this.racks, that.racks);
    }
    return false;
  }
}
