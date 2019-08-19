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
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import java.util.Objects;

public final class RackConstant extends AbstractEntity {

  private String library;
  private String name;
  private String expression;

  public RackConstant(@NotNull String library, @NotNull String name, @NotNull String expression) {
    this.library = library;
    this.name = name;
    this.expression = expression;
  }

  public RackConstant(Element element) {
    super(element);
    this.name = element.getAttribute("name");
    this.expression = element.getTextContent();
  }

  public RackConstant(InputSource source) {
    this(element(source));
  }

  @NotNull
  @Override
  public String getTag() {
    return "const";
  }

  @Override
  public void writeTo(@NotNull Element element) {
    element.setAttribute("library", library);
    element.setTextContent(expression);
  }

  public String getExpression() {
    return expression;
  }

  public void setExpression(String expression) {
    this.expression = expression;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getLibrary() {
    return library;
  }

  public void setLibrary(String library) {
    this.library = library;
  }

  @Override
  public int hashCode() {
    return Objects.hash(library, name, expression);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof RackConstant) {
      final var that = (RackConstant) obj;
      return Objects.equals(this.library, that.library)
          && Objects.equals(this.name, that.name)
          && Objects.equals(this.expression, that.expression);
    }
    return false;
  }
}
