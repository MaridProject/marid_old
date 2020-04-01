package org.marid.model;

import org.w3c.dom.Element;

public interface Null extends ConstantArgument {

  @Override
  default String tag() {
    return "null";
  }

  @Override default void readFrom(Element element) {}
  @Override default void writeTo(Element element) {}
}
