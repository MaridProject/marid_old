package org.marid.runtime.model;

import org.w3c.dom.Element;

import java.util.List;

public interface Rack extends Entity, HasVarargs {

  String getName();

  void setName(String name);

  String getFactory();

  void setFactory(String factory);

  List<? extends Argument> getArguments();

  List<? extends Initializer> getInitializers();

  void addArgument(Argument argument);

  void addInitializer(Initializer initializer);

  @Override
  default String tag() {
    return "rack";
  }

  @Override
  default void readFrom(Element element) {
    setName(element.getAttribute("name"));
    setFactory(element.getAttribute("factory"));
    ModelObjectFactoryFriend.children(element).forEach(e -> {
      final var c = modelObjectFactory().newEntity(e.getTagName());
      c.readFrom(e);
      if (c instanceof Argument) {
        addArgument((Argument) c);
      } else if (c instanceof Initializer) {
        addInitializer((Initializer) c);
      }
    });
  }

  @Override
  default void writeTo(Element element) {
    element.setAttribute("name", getName());
    element.setAttribute("factory", getFactory());
    for (final var argument: getArguments()) {
      final var e = element.getOwnerDocument().createElement(argument.tag());
      element.appendChild(e);
      argument.writeTo(e);
    }
    for (final var initializer: getInitializers()) {
      final var e = element.getOwnerDocument().createElement(initializer.tag());
      element.appendChild(e);
      initializer.writeTo(e);
    }
  }
}
