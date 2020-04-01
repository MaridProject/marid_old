package org.marid.model;

import org.w3c.dom.Element;

public interface ConstRef extends ConstantArgument {

  String getCellar();

  void setCellar(String cellar);

  String getRef();

  void setRef(String ref);

  @Override
  default String tag() {
    return "const-ref";
  }

  @Override
  default void readFrom(Element element) {
    setCellar(element.getAttribute("cellar"));
    setRef(element.getAttribute("ref"));
  }

  @Override
  default void writeTo(Element element) {
    element.setAttribute("cellar", getCellar());
    element.setAttribute("ref", getRef());
  }
}
