package org.marid.model;

import org.w3c.dom.Element;

import java.util.List;

public interface Initializer extends Entity, HasVarargs {

  String getName();

  void setName(String name);

  List<? extends Argument> getArguments();

  void addArgument(Argument argument);

  @Override
  default String tag() {
    return "init";
  }

  @Override
  default void readFrom(Element element) {
    setName(element.getAttribute("name"));
    ModelObjectFactoryFriend.children(element).forEach(e -> {
      final var c = modelObjectFactory().newEntity(e.getTagName());
      c.readFrom(e);
      if (c instanceof Argument) {
        addArgument((Argument) c);
      }
    });
  }

  @Override
  default void writeTo(Element element) {
    element.setAttribute("name", getName());
    for (final var argument : getArguments()) {
      final var e = element.getOwnerDocument().createElement(argument.tag());
      element.appendChild(e);
      argument.writeTo(e);
    }
  }
}
