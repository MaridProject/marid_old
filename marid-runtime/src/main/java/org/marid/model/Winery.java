package org.marid.model;

import org.w3c.dom.Element;

import java.util.List;

public interface Winery extends Entity {

  String getGroup();

  void setGroup(String group);

  String getName();

  void setName(String name);

  String getVersion();

  void setVersion(String version);

  List<? extends Cellar> getCellars();

  void addCellar(Cellar cellar);

  @Override
  default String tag() {
    return "winery";
  }

  @Override
  default void readFrom(Element element) {
    setGroup(element.getAttribute("group"));
    setName(element.getAttribute("name"));
    setVersion(element.getAttribute("version"));
    ModelObjectFactoryFriend.children(element).forEach(e -> {
      final var c = modelObjectFactory().newEntity(e.getTagName());
      c.readFrom(e);
      if (c instanceof Cellar) {
        addCellar((Cellar) c);
      }
    });
  }

  @Override
  default void writeTo(Element element) {
    element.setAttribute("group", getGroup());
    element.setAttribute("name", getName());
    element.setAttribute("version", getVersion());
    for (final var cellar : getCellars()) {
      final var e = element.getOwnerDocument().createElement(cellar.tag());
      element.appendChild(e);
      cellar.writeTo(e);
    }
  }
}
