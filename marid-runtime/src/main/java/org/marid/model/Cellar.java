package org.marid.model;

import org.w3c.dom.Element;

import java.util.List;

public interface Cellar extends Entity {

  String getName();

  void setName(String name);

  List<? extends CellarConstant> getConstants();

  List<? extends Rack> getRacks();

  void addRack(Rack rack);

  void addConstant(CellarConstant constant);

  @Override
  default String tag() {
    return "cellar";
  }

  @Override
  default void readFrom(Element element) {
    setName(element.getAttribute("name"));
    ModelObjectFactoryFriend.children(element).forEach(e -> {
      final var c = modelObjectFactory().newEntity(e.getTagName());
      c.readFrom(e);
      if (c instanceof CellarConstant) {
        addConstant((CellarConstant) c);
      } else if (c instanceof Rack) {
        addRack((Rack) c);
      }
    });
  }

  @Override
  default void writeTo(Element element) {
    element.setAttribute("name", getName());
    for (final var constant: getConstants()) {
      final var e = element.getOwnerDocument().createElement(constant.tag());
      element.appendChild(e);
      constant.writeTo(e);
    }
    for (final var rack: getRacks()) {
      final var e = element.getOwnerDocument().createElement(rack.tag());
      element.appendChild(e);
      rack.writeTo(e);
    }
  }
}
