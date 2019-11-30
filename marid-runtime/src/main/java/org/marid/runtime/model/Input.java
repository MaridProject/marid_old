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

import java.util.Objects;

public final class Input extends AbstractEntity {

  private String name;
  private Argument argument;

  public Input(@NotNull String name, @NotNull Argument argument) {
    this.name = name;
    this.argument = argument;
  }

  public Input(@NotNull Element element) {
    super(element);
    this.name = element.getAttribute("name");
    this.argument = XmlStreams.elementsByTag(element, "arg").findFirst().map(Argument::argument).orElseThrow();
  }

  public Input(@NotNull InputSource inputSource) {
    this(element(inputSource));
  }

  @Override
  public @NotNull String getTag() {
    return "in";
  }

  public @NotNull String getName() {
    return name;
  }

  public @NotNull Input setName(String name) {
    this.name = name;
    return this;
  }

  public @NotNull Argument getArgument() {
    return argument;
  }

  public @NotNull Input setArgument(Argument argument) {
    this.argument = argument;
    return this;
  }

  @Override
  public void writeTo(@NotNull Element element) {
    element.setAttribute("name", name);
    XmlUtils.append(element, "arg", argument::writeTo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, argument);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof Input) {
      final var that = (Input) obj;
      return Objects.equals(this.name, that.name) && Objects.equals(this.argument, that.argument);
    }
    return false;
  }
}
