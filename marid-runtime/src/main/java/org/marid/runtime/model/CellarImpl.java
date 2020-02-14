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
import java.util.NoSuchElementException;
import java.util.Objects;

import static java.util.stream.Collectors.toCollection;

public final class CellarImpl extends AbstractEntity {

  private final ArrayList<RackImpl> racks;
  private final ArrayList<CellarConstantImpl> constants;

  private String name;

  public CellarImpl(@NotNull String name) {
    this.name = name;
    this.racks = new ArrayList<>();
    this.constants = new ArrayList<>();
  }

  public CellarImpl(@NotNull Element element) {
    super(element);
    this.name = element.getAttribute("name");
    this.constants = XmlStreams.children(element, CellarConstantImpl::new).collect(toCollection(ArrayList::new));
    this.racks = XmlStreams.elementsByTag(element, "rack").map(RackImpl::new).collect(toCollection(ArrayList::new));
  }

  public CellarImpl(@NotNull InputSource source) {
    this(element(source));
  }

  @NotNull
  public CellarImpl addConstant(CellarConstantImpl constant) {
    constants.add(constant);
    return this;
  }

  @NotNull
  public CellarImpl addRack(RackImpl rack) {
    racks.add(rack);
    return this;
  }

  public String getName() {
    return name;
  }

  @NotNull
  public CellarImpl setName(String name) {
    this.name = name;
    return this;
  }

  public ArrayList<CellarConstantImpl> getConstants() {
    return constants;
  }

  @NotNull
  public CellarConstantImpl getConstant(@NotNull String name) {
    return constants.stream()
        .filter(c -> name.equals(c.getName()))
        .findFirst()
        .orElseThrow(() -> new NoSuchElementException(name));
  }

  @NotNull
  public ArrayList<RackImpl> getRacks() {
    return racks;
  }

  @NotNull
  public RackImpl getRack(@NotNull String name) {
    return racks.stream()
        .filter(r -> name.equals(r.getName()))
        .findFirst()
        .orElseThrow(() -> new NoSuchElementException(name));
  }

  @Override
  public void writeTo(@NotNull Element element) {
    element.setAttribute("name", name);
    constants.forEach(r -> XmlUtils.appendTo(r, element));
    racks.forEach(r -> XmlUtils.appendTo(r, element));
  }

  @NotNull
  @Override
  public String getTag() {
    return "cellar";
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, constants, racks);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof CellarImpl) {
      final var that = (CellarImpl) obj;
      return Objects.equals(this.name, that.name)
          && Objects.equals(this.constants, that.constants)
          && Objects.equals(this.racks, that.racks);
    }
    return false;
  }
}
