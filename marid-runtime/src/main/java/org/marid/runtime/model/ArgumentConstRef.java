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
import org.w3c.dom.Element;

import java.util.Objects;

public final class ArgumentConstRef extends Argument {

  private String cellar;
  private String name;

  public ArgumentConstRef(@NotNull String cellar, @NotNull String name) {
    this.cellar = cellar;
    this.name = name;
  }

  public ArgumentConstRef(@NotNull Element element) {
    super(element);
    this.cellar = element.getAttribute("cellar");
    this.name = element.getAttribute("name");
  }

  @Override
  public @NotNull String getTag() {
    return "const-ref";
  }

  @Override
  public void writeTo(@NotNull Element element) {
    element.setAttribute("cellar", cellar);
    element.setAttribute("name", name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(cellar, name);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof ArgumentConstRef) {
      final var that = (ArgumentConstRef) obj;
      return Objects.equals(this.cellar, that.cellar) && Objects.equals(this.name, that.name);
    }
    return false;
  }
}
