package org.marid.runtime.model;

import org.w3c.dom.Element;

import java.util.List;

public interface CellarConstant extends Entity, HasVarargs {

  String getFactory();

  void setFactory(String factory);

  String getSelector();

  void setSelector(String selector);

  String getName();

  void setName(String name);

  List<? extends ConstantArgument> getArguments();

  void addArgument(ConstantArgument argument);

  @Override
  default String tag() {
    return "const";
  }

  @Override
  default void readFrom(Element element) {
    setFactory(element.getAttribute("factory"));
    setSelector(element.getAttribute("selector"));
    setName(element.getAttribute("name"));
    ModelObjectFactoryFriend.children(element).forEach(e -> {
      final var c = modelObjectFactory().newEntity(e.getTagName());
      c.readFrom(e);
      if (c instanceof ConstantArgument) {
        addArgument((ConstantArgument) c);
      }
    });
  }

  @Override
  default void writeTo(Element element) {
    element.setAttribute("name", getName());
    element.setAttribute("factory", getFactory());
    element.setAttribute("selector", getSelector());
    for (final var argument: getArguments()) {
      final var e = element.getOwnerDocument().createElement(argument.tag());
      element.appendChild(e);
      argument.writeTo(e);
    }
  }
}
