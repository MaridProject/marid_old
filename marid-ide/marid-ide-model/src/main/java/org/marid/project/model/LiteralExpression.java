package org.marid.project.model;

import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.CharLiteralExpr;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import java.util.Objects;
import java.util.function.Function;

import static com.github.javaparser.ast.type.PrimitiveType.byteType;
import static com.github.javaparser.ast.type.PrimitiveType.shortType;

public class LiteralExpression extends AbstractEntity {

  private final Type type;
  private String value;

  public LiteralExpression(Type type, String value) {
    this.type = type;
    this.value = value;
  }

  public LiteralExpression(Element element) {
    super(element);
    type = Type.valueOf(element.getTagName().toUpperCase());
    value = element.getTextContent();
  }

  public LiteralExpression(InputSource inputSource) {
    this(element(inputSource));
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
    if (obj instanceof LiteralExpression) {
      final var that = (LiteralExpression) obj;
      return Objects.equals(this.type, that.type)
          && Objects.equals(this.value, that.value);
    }
    return false;
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
    STRING(String.class, StringLiteralExpr::new);

    public final Class<?> type;
    public final Function<String, Expression> ast;

    Type(Class<?> type, Function<String, Expression> ast) {
      this.type = type;
      this.ast = ast;
    }
  }
}
