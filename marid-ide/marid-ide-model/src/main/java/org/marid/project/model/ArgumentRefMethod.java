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

import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import java.util.Objects;

public final class ArgumentRefMethod extends Argument {

  private String cellar;
  private String rack;
  private String method;

  public ArgumentRefMethod(@NotNull String cellar, @NotNull String rack, @NotNull String method) {
    this.cellar = cellar;
    this.rack = rack;
    this.method = method;
  }

  public ArgumentRefMethod(@NotNull Element element) {
    super(element);
    this.cellar = element.getAttribute("cellar");
    this.rack = element.getAttribute("rack");
    this.method = element.getAttribute("method");
  }

  public ArgumentRefMethod(@NotNull InputSource inputSource) {
    this(element(inputSource));
  }

  @Override
  public MethodCallExpr getExpression() {
    return new MethodCallExpr(new ClassExpr(new ClassOrInterfaceType(null, cellar + "." + rack)), method);
  }

  @Override
  public @NotNull String getTag() {
    return "ref-method";
  }

  public String getCellar() {
    return cellar;
  }

  public void setCellar(String cellar) {
    this.cellar = cellar;
  }

  public String getRack() {
    return rack;
  }

  public void setRack(String rack) {
    this.rack = rack;
  }

  public String getMethod() {
    return method;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  @Override
  public void writeTo(@NotNull Element element) {
    element.setAttribute("cellar", cellar);
    element.setAttribute("rack", rack);
    element.setAttribute("method", method);
  }

  @Override
  public int hashCode() {
    return Objects.hash(cellar, rack, method);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof ArgumentRefMethod) {
      final var that = (ArgumentRefMethod) obj;
      return Objects.equals(this.cellar, that.cellar)
          && Objects.equals(this.rack, that.rack)
          && Objects.equals(this.method, that.method);
    }
    return false;
  }
}
