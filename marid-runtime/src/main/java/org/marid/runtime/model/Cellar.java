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

import static java.util.stream.Collectors.toCollection;

public final class Cellar extends AbstractEntity {

  private final ArrayList<Rack> racks;
  private final ArrayList<CellarConstant> constants;

  private String name;

  public Cellar(@NotNull String name) {
    this.name = name;
    this.racks = new ArrayList<>();
    this.constants = new ArrayList<>();
  }

  public Cellar(@NotNull Element element) {
    super(element);
    this.name = element.getAttribute("name");
    this.constants = XmlStreams.children(element, CellarConstant::new).collect(toCollection(ArrayList::new));
    this.racks = XmlStreams.elementsByTag(element, "rack").map(Rack::new).collect(toCollection(ArrayList::new));
  }

  public Cellar(@NotNull InputSource source) {
    this(element(source));
  }

  public Cellar addConstant(CellarConstant constant) {
    constants.add(constant);
    return this;
  }

  public Cellar addRack(Rack rack) {
    racks.add(rack);
    return this;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ArrayList<CellarConstant> getConstants() {
    return constants;
  }

  public ArrayList<Rack> getRacks() {
    return racks;
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
    if (obj instanceof Cellar) {
      final var that = (Cellar) obj;
      return Objects.equals(this.name, that.name)
          && Objects.equals(this.constants, that.constants)
          && Objects.equals(this.racks, that.racks);
    }
    return false;
  }
}
