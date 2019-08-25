package org.marid.project.model;

import org.w3c.dom.Element;
import org.xml.sax.InputSource;

interface ArgumentFactory {

  static Argument argument(Element element) {
    try {
      return new ArgumentLiteral(element);
    } catch (IllegalArgumentException e) {
      switch (element.getTagName()) {
        case "ref-method":
          return new ArgumentRefMethod(element);
        case "ref-field":
          return new ArgumentRefField(element);
        default:
          throw new IllegalArgumentException(element.getTagName());
      }
    }
  }

  static Argument argument(InputSource inputSource) {
    return argument(AbstractEntity.element(inputSource));
  }
}
