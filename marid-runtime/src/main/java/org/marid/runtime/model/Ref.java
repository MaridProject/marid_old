package org.marid.runtime.model;

import org.w3c.dom.Element;

public interface Ref extends Argument {

  String getCellar();

  void setCellar(String cellar);

  String getRack();

  void setRack(String rack);

  String getRef();

  void setRef(String ref);

  @Override
  default String tag() {
    return "ref";
  }

  @Override
  default void readFrom(Element element) {
    setName(element.getAttribute("name"));
    setCellar(element.getAttribute("cellar"));
    setRack(element.getAttribute("rack"));
    setRef(element.getAttribute("ref"));
  }

  @Override
  default void writeTo(Element element) {
    element.setAttribute("name", getName());
    element.setAttribute("cellar", getCellar());
    element.setAttribute("rack", getRack());
    element.setAttribute("ref", getRef());
  }
}
