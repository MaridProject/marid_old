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
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import java.util.Objects;
import java.util.function.Function;

public final class ArgumentRef extends Argument {

  private Type type;
  private String cellar;
  private String ref;

  public ArgumentRef(@NotNull Type type, @NotNull String cellar, @NotNull String ref) {
    this.type = type;
    this.cellar = cellar;
    this.ref = ref;
  }

  public ArgumentRef(@NotNull Element element) {
    super(element);
    this.type = Type.valueOf(element.getAttribute("type").toUpperCase());
    this.cellar = element.getAttribute("cellar");
    this.ref = element.getAttribute("ref");
  }

  public ArgumentRef(@NotNull InputSource inputSource) {
    this(element(inputSource));
  }

  @Override
  public Expression getExpression() {
    return type.ast.apply(this);
  }

  @Override
  public @NotNull String getTag() {
    return "ref";
  }

  public String getCellar() {
    return cellar;
  }

  public void setCellar(String cellar) {
    this.cellar = cellar;
  }

  public String getRef() {
    return ref;
  }

  public void setRef(String ref) {
    this.ref = ref;
  }

  @Override
  public void writeTo(@NotNull Element element) {
    element.setAttribute("type", type.name().toLowerCase());
    element.setAttribute("cellar", cellar);
    element.setAttribute("ref", ref);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, cellar, ref);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof ArgumentRef) {
      final var that = (ArgumentRef) obj;
      return Objects.equals(this.type, that.type)
          && Objects.equals(this.cellar, that.cellar)
          && Objects.equals(this.ref, that.ref);
    }
    return false;
  }

  public enum Type {
    CONST(a -> new FieldAccessExpr(new ClassExpr(new ClassOrInterfaceType(null, a.cellar)), a.ref)),
    LOCAL(a -> new MethodCallExpr(new NameExpr(a.ref), "get"));

    private final Function<ArgumentRef, Expression> ast;

    Type(Function<ArgumentRef, Expression> ast) {
      this.ast = ast;
    }
  }
}
