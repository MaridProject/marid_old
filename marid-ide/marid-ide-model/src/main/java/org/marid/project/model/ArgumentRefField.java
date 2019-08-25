package org.marid.project.model;

import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import java.util.Objects;

public final class ArgumentRefField extends Argument {

  private String cellar;
  private String rack;
  private String field;

  public ArgumentRefField(@NotNull String cellar, @NotNull String rack, @NotNull String field) {
    this.cellar = cellar;
    this.rack = rack;
    this.field = field;
  }

  public ArgumentRefField(@NotNull Element element) {
    super(element);
    this.cellar = element.getAttribute("cellar");
    this.rack = element.getAttribute("rack");
    this.field = element.getAttribute("field");
  }

  public ArgumentRefField(@NotNull InputSource inputSource) {
    this(element(inputSource));
  }

  @Override
  public Expression getExpression() {
    return new FieldAccessExpr(new ClassExpr(new ClassOrInterfaceType(null, cellar + "." + rack)), field);
  }

  @Override
  public @NotNull String getTag() {
    return "ref-field";
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

  public String getField() {
    return field;
  }

  public void setField(String field) {
    this.field = field;
  }

  @Override
  public void writeTo(@NotNull Element element) {
    element.setAttribute("cellar", cellar);
    element.setAttribute("rack", rack);
    element.setAttribute("field", field);
  }

  @Override
  public int hashCode() {
    return Objects.hash(cellar, rack, field);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof ArgumentRefField) {
      final var that = (ArgumentRefField) obj;
      return Objects.equals(this.cellar, that.cellar)
          && Objects.equals(this.rack, that.rack)
          && Objects.equals(this.field, that.field);
    }
    return false;
  }
}
