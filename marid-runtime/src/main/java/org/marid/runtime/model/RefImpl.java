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
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import java.util.Objects;

public final class RefImpl extends ArgumentImpl {

  private String cellar;
  private String rack;
  private String ref;

  public RefImpl(@Nullable String cellar, @NotNull String rack, @NotNull String ref) {
    this.cellar = cellar;
    this.rack = rack;
    this.ref = ref;
  }

  public RefImpl(@NotNull Element element) {
    super(element);
    this.cellar = element.getAttribute("cellar");
    this.rack = element.getAttribute("rack");
    this.ref = element.getAttribute("ref");
  }

  public RefImpl(@NotNull InputSource inputSource) {
    this(element(inputSource));
  }

  @Override
  public @NotNull String getTag() {
    return "ref";
  }

  public String getRef() {
    return ref;
  }

  public @NotNull RefImpl setRef(@NotNull String ref) {
    this.ref = ref;
    return this;
  }

  public @Nullable String getCellar() {
    return cellar;
  }

  public @NotNull RefImpl setCellar(@Nullable String cellar) {
    this.cellar = cellar;
    return this;
  }

  public @NotNull String getRack() {
    return rack;
  }

  public @NotNull RefImpl setRack(@NotNull String rack) {
    this.rack = rack;
    return this;
  }

  @Override
  public void writeTo(@NotNull Element element) {
    if (cellar != null) {
      element.setAttribute("cellar", cellar);
    } else {
      element.removeAttribute("cellar");
    }
    element.setAttribute("rack", rack);
    element.setAttribute("ref", ref);
  }

  @Override
  public int hashCode() {
    return Objects.hash(cellar, ref);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof RefImpl) {
      final var that = (RefImpl) obj;
      return Objects.equals(this.cellar, that.cellar) && Objects.equals(this.ref, that.ref);
    }
    return false;
  }
}
