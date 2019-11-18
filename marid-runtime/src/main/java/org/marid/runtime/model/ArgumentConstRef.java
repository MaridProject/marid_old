package org.marid.runtime.model;

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
