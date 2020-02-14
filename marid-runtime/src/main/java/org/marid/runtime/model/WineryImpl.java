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
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

public final class WineryImpl extends AbstractEntity {

  private final ArrayList<CellarImpl> cellars;
  private String group;
  private String name;
  private String version;

  public WineryImpl(@NotNull String group, @NotNull String name, @NotNull String version) {
    this.group = group;
    this.name = name;
    this.version = version;
    this.cellars = new ArrayList<>();
  }

  public WineryImpl(@NotNull Element element) {
    super(element);
    this.group = element.getAttribute("group");
    this.name = element.getAttribute("name");
    this.version = element.getAttribute("version");
    this.cellars = XmlStreams.children(element, CellarImpl::new).collect(Collectors.toCollection(ArrayList::new));
  }

  public WineryImpl(@NotNull InputSource inputSource) {
    this(element(inputSource));
  }

  @NotNull
  public WineryImpl addCellar(@NotNull CellarImpl cellar) {
    cellars.add(cellar);
    return this;
  }

  @NotNull
  public List<CellarImpl> getCellars() {
    return cellars;
  }

  @NotNull
  public CellarImpl getCellar(@NotNull String name) {
    return cellars.stream()
        .filter(c -> name.equals(c.getName()))
        .findFirst()
        .orElseThrow(() -> new NoSuchElementException(name));
  }

  @Override
  public void writeTo(@NotNull Element element) {
    element.setAttribute("group", group);
    element.setAttribute("name", name);
    element.setAttribute("version", version);
    cellars.forEach(c -> XmlUtils.appendTo(c, element));
  }

  @NotNull public String getGroup() { return group; }
  @NotNull public WineryImpl setGroup(String group) { this.group = group; return this; }
  @NotNull public String getName() { return name; }
  @NotNull public WineryImpl setName(String name) { this.name = name; return this; }
  @NotNull public String getVersion() { return version; }
  @NotNull public WineryImpl setVersion(String version) { this.version = version; return this; }

  @NotNull
  @Override
  public String getTag() {
    return "winery";
  }

  @Override
  public int hashCode() {
    return Objects.hash(group, name, version, cellars);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof WineryImpl) {
      final var that = (WineryImpl) obj;

      return Objects.equals(this.group, that.group)
          && Objects.equals(this.name, that.name)
          && Objects.equals(this.version, that.version)
          && Objects.equals(this.cellars, that.cellars);
    }
    return false;
  }
}
