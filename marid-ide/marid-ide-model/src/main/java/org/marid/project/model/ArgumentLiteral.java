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

import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.CharLiteralExpr;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import java.util.Objects;
import java.util.function.Function;

import static com.github.javaparser.ast.type.PrimitiveType.byteType;
import static com.github.javaparser.ast.type.PrimitiveType.shortType;

public final class ArgumentLiteral extends Argument {

  private final Type type;
  private String value;

  public ArgumentLiteral(Type type, String value) {
    this.type = type;
    this.value = value;
  }

  public ArgumentLiteral(@NotNull Element element) {
    super(element);
    type = Type.valueOf(element.getTagName().toUpperCase());
    value = element.getTextContent();
  }

  public ArgumentLiteral(@NotNull InputSource inputSource) {
    this(element(inputSource));
  }

  @Override
  public Expression getExpression() {
    return type.ast.apply(value);
  }

  public Type getType() {
    return type;
  }

  @Override
  public @NotNull String getTag() {
    return type.name().toLowerCase();
  }

  @Override
  public void writeTo(@NotNull Element element) {
    element.setTextContent(value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, value);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof ArgumentLiteral) {
      final var that = (ArgumentLiteral) obj;
      return Objects.equals(this.type, that.type)
          && Objects.equals(this.value, that.value);
    }
    return false;
  }

  public enum Type {

    BYTE(byte.class, value -> new CastExpr(byteType(), new IntegerLiteralExpr(value))),
    SHORT(short.class, value -> new CastExpr(shortType(), new IntegerLiteralExpr(value))),
    INT(int.class, IntegerLiteralExpr::new),
    LONG(long.class, LongLiteralExpr::new),
    FLOAT(float.class, DoubleLiteralExpr::new),
    DOUBLE(double.class, DoubleLiteralExpr::new),
    CHAR(char.class, CharLiteralExpr::new),
    BOOLEAN(boolean.class, value -> new BooleanLiteralExpr("true".equalsIgnoreCase(value))),
    STRING(String.class, StringLiteralExpr::new),
    CLASS(Class.class, value -> new ClassExpr(new ClassOrInterfaceType(null, value)));

    public final Class<?> type;
    public final Function<String, Expression> ast;

    Type(Class<?> type, Function<String, Expression> ast) {
      this.type = type;
      this.ast = ast;
    }
  }
}
