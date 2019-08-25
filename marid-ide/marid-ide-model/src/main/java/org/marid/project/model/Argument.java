package org.marid.project.model;

import com.github.javaparser.ast.expr.Expression;
import org.w3c.dom.Element;

public abstract class Argument extends AbstractEntity {

  Argument() {
  }

  Argument(Element element) {
    super(element);
  }

  public abstract Expression getExpression();
}
