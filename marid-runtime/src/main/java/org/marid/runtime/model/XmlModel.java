package org.marid.runtime.model;

import org.w3c.dom.Element;

import java.util.stream.IntStream;
import java.util.stream.Stream;

@SuppressWarnings("SwitchStatementWithTooFewBranches")
public class XmlModel {

  private XmlModel() {}

  public static void write(Winery winery, Element element) {
    element.setAttribute("group", winery.getGroup());
    element.setAttribute("name", winery.getName());
    element.setAttribute("version", winery.getVersion());

    for (final var cellar : winery.getCellars()) {
      final var cellarElement = element.getOwnerDocument().createElement("cellar");
      write(cellar, cellarElement);
      element.appendChild(cellarElement);
    }
  }

  public static void write(Cellar cellar, Element element) {
    element.setAttribute("name", cellar.getName());

    for (final var cellarConstant : cellar.getConstants()) {
      final var cellarConstantElement = element.getOwnerDocument().createElement("const");
      write(cellarConstant, cellarConstantElement);
      element.appendChild(cellarConstantElement);
    }

    for (final var rack : cellar.getRacks()) {
      final var rackElement = element.getOwnerDocument().createElement("rack");
      write(rack, rackElement);
      element.appendChild(rackElement);
    }
  }

  public static void write(Rack rack, Element element) {
    element.setAttribute("factory", rack.getFactory());
    element.setAttribute("name", rack.getName());

    for (final var argument : rack.getArguments()) {
      final var argumentElement = element.getOwnerDocument().createElement("arg");
      write(argument, argumentElement);
      element.appendChild(argumentElement);
    }

    for (final var initializer : rack.getInitializers()) {
      final var initializerElement = element.getOwnerDocument().createElement("init");
      write(initializer, initializerElement);
      element.appendChild(initializerElement);
    }
  }

  public static void write(CellarConstant cellarConstant, Element element) {
    element.setAttribute("factory", cellarConstant.getFactory());
    element.setAttribute("selector", cellarConstant.getSelector());
    element.setAttribute("name", cellarConstant.getName());

    for (final var argument : cellarConstant.getArguments()) {
      final var argumentElement = element.getOwnerDocument().createElement("arg");
      write(argument, argumentElement);
      element.appendChild(argumentElement);
    }
  }

  public static void write(ConstantArgument argument, Element element) {
    if (argument instanceof ConstRef) {
      element.setAttribute("name", argument.getName());
      element.setAttribute("cellar", ((ConstRef) argument).getCellar());
      element.setAttribute("ref", ((ConstRef) argument).getRef());
    } else if (argument instanceof Literal) {
      element.setAttribute("name", argument.getName());
      element.setAttribute("type", ((Literal) argument).getType().name());
      element.setTextContent(((Literal) argument).getValue());
    } else {
      element.setAttribute("name", argument.getName());
    }
  }

  public static void write(Argument argument, Element element) {
    if (argument instanceof Ref) {
      element.setAttribute("name", argument.getName());
      element.setAttribute("cellar", ((Ref) argument).getCellar());
      element.setAttribute("rack", ((Ref) argument).getRack());
      element.setAttribute("ref", ((Ref) argument).getRef());
    } else if (argument instanceof ConstantArgument) {
      write((ConstantArgument) argument, element);
    }
  }

  public static void write(Initializer initializer, Element element) {
    element.setAttribute("name", initializer.getName());

    for (final var argument : initializer.getArguments()) {
      final var argumentElement = element.getOwnerDocument().createElement("arg");
      write(argument, argumentElement);
      element.appendChild(argumentElement);
    }
  }

  public static Winery readWinery(ModelObjectFactory factory, Element element) {
    final var winery = factory.newWinery();
    read(winery, factory, element);
    return winery;
  }

  public static void read(Winery winery, ModelObjectFactory factory, Element element) {
    winery.setGroup(element.getAttribute("group"));
    winery.setName(element.getAttribute("name"));
    winery.setVersion(element.getAttribute("version"));

    children(element).forEach(e -> {
      switch (e.getTagName()) {
        case "cellar":
          winery.addCellar(readCellar(factory, e));
          break;
      }
    });
  }

  public static Cellar readCellar(ModelObjectFactory factory, Element element) {
    final var cellar = factory.newCellar();
    read(cellar, factory, element);
    return cellar;
  }

  public static void read(Cellar cellar, ModelObjectFactory factory, Element element) {
    cellar.setName(element.getAttribute("name"));

    children(element).forEach(e -> {
      switch (e.getTagName()) {
        case "const":
          cellar.addConstant(readCellarConstant(factory, e));
          break;
        case "rack":
          cellar.addRack(readRack(factory, e));
          break;
      }
    });
  }

  public static CellarConstant readCellarConstant(ModelObjectFactory factory, Element element) {
    final var constant = factory.newCellarConstant();
    read(constant, factory, element);
    return constant;
  }

  public static void read(CellarConstant constant, ModelObjectFactory factory, Element element) {
    constant.setFactory(element.getAttribute("factory"));
    constant.setSelector(element.getAttribute("selector"));
    constant.setName(element.getAttribute("name"));
    children(element).forEach(e -> {
      switch (e.getTagName()) {
        case "arg":
          constant.addArgument(readConstantArgument(factory, e));
          break;
      }
    });
  }

  public static ConstantArgument readConstantArgument(ModelObjectFactory factory, Element element) {
    if (element.hasAttribute("type")) {
      final var literal = factory.newLiteral();
      read(literal, element);
      return literal;
    } else if (element.hasAttribute("cellar") && element.hasAttribute("ref")) {
      final var constRef = factory.newConstRef();
      read(constRef, element);
      return constRef;
    } else {
      return factory.newNull();
    }
  }

  public static void read(Literal literal, Element element) {
    literal.setType(Literal.Type.valueOf(element.getAttribute("type").toUpperCase()));
    literal.setValue(element.getTextContent());
  }

  public static void read(ConstRef constRef, Element element) {
    constRef.setCellar(element.getAttribute("cellar"));
    constRef.setRef(element.getAttribute("ref"));
  }

  public static Argument readArgument(ModelObjectFactory factory, Element element) {
    if (element.hasAttribute("rack") && element.hasAttribute("ref")) {
      final var ref = factory.newRef();
      read(ref, element);
      return ref;
    } else {
      return readConstantArgument(factory, element);
    }
  }

  public static void read(Ref ref, Element element) {
    ref.setCellar(element.getAttribute("cellar"));
    ref.setRack(element.getAttribute("rack"));
    ref.setRef(element.getAttribute("ref"));
  }

  public static Rack readRack(ModelObjectFactory factory, Element element) {
    final var rack = factory.newRack();
    read(rack, factory, element);
    return rack;
  }

  public static void read(Rack rack, ModelObjectFactory factory, Element element) {
    rack.setName(element.getAttribute("name"));
    rack.setFactory(element.getAttribute("factory"));
    children(element).forEach(e -> {
      switch (e.getTagName()) {
        case "arg":
          rack.addArgument(readArgument(factory, e));
          break;
        case "init":
          rack.addInitializer(readInitializer(factory, e));
          break;
      }
    });
  }

  public static Initializer readInitializer(ModelObjectFactory factory, Element element) {
    final var initializer = factory.newInitializer();
    read(initializer, factory, element);
    return initializer;
  }

  public static void read(Initializer initializer, ModelObjectFactory factory, Element element) {
    initializer.setName(element.getAttribute("name"));
    children(element).forEach(e -> {
      switch (e.getTagName()) {
        case "arg":
          initializer.addArgument(readArgument(factory, e));
          break;
      }
    });
  }

  private static Stream<Element> children(Element element) {
    final var list = element.getChildNodes();
    return IntStream.range(0, list.getLength())
      .mapToObj(list::item)
      .filter(Element.class::isInstance)
      .map(Element.class::cast);
  }
}
