package org.marid.runtime.model;

import org.w3c.dom.Element;

public interface Output extends Entity {

  String getName();

  void setName(String name);

  @Override
  default String tag() {
    return "output";
  }

  @Override
  default void readFrom(Element element) {
    setName(element.getAttribute("name"));
  }

  @Override
  default void writeTo(Element element) {
    element.setAttribute("name", getName());
  }
}
