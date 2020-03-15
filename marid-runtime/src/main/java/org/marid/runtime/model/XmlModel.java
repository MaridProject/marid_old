package org.marid.runtime.model;

import jdk.dynalink.CallSiteDescriptor;
import jdk.dynalink.DynamicLinker;
import jdk.dynalink.DynamicLinkerFactory;
import jdk.dynalink.StandardNamespace;
import jdk.dynalink.StandardOperation;
import jdk.dynalink.beans.StaticClass;
import jdk.dynalink.support.SimpleRelinkableCallSite;
import org.w3c.dom.Element;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class XmlModel {

  private static final DynamicLinker LINKER = new DynamicLinkerFactory().createLinker();

  private XmlModel() {}

  public static void write(Winery winery, Element element) {
    element.setAttribute("group", winery.getGroup());
    element.setAttribute("name", winery.getName());
    element.setAttribute("version", winery.getVersion());

    for (final var cellar : winery.getCellars()) {
      final var cellarElement = element.getOwnerDocument().createElement(cellar.tag());
      write(cellar, cellarElement);
      element.appendChild(cellarElement);
    }
  }

  public static void write(Cellar cellar, Element element) {
    element.setAttribute("name", cellar.getName());

    for (final var cellarConstant : cellar.getConstants()) {
      final var cellarConstantElement = element.getOwnerDocument().createElement(cellarConstant.tag());
      write(cellarConstant, cellarConstantElement);
      element.appendChild(cellarConstantElement);
    }

    for (final var rack : cellar.getRacks()) {
      final var rackElement = element.getOwnerDocument().createElement(rack.tag());
      write(rack, rackElement);
      element.appendChild(rackElement);
    }
  }

  public static void write(Rack rack, Element element) {
    element.setAttribute("factory", rack.getFactory());
    element.setAttribute("name", rack.getName());

    for (final var argument : rack.getArguments()) {
      final var argumentElement = element.getOwnerDocument().createElement(argument.tag());
      write(argument, argumentElement);
      element.appendChild(argumentElement);
    }

    for (final var initializer : rack.getInitializers()) {
      final var initializerElement = element.getOwnerDocument().createElement(initializer.tag());
      write(initializer, initializerElement);
      element.appendChild(initializerElement);
    }
  }

  public static void write(CellarConstant cellarConstant, Element element) {
    element.setAttribute("factory", cellarConstant.getFactory());
    element.setAttribute("selector", cellarConstant.getSelector());
    element.setAttribute("name", cellarConstant.getName());

    for (final var argument : cellarConstant.getArguments()) {
      final var argumentElement = element.getOwnerDocument().createElement(argument.tag());
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
      final var argumentElement = element.getOwnerDocument().createElement(argument.tag());
      write(argument, argumentElement);
      element.appendChild(argumentElement);
    }
  }

  public static Winery readWinery(ModelObjectFactory factory, Element element) {
    final var winery = factory.newWinery();
    read(winery, element);
    return winery;
  }

  public static void read(Winery winery, Element element) {
    winery.setGroup(element.getAttribute("group"));
    winery.setName(element.getAttribute("name"));
    winery.setVersion(element.getAttribute("version"));
    addElements(winery, element);
  }

  public static Cellar readCellar(ModelObjectFactory factory, Element element) {
    final var cellar = factory.newCellar();
    read(cellar, element);
    return cellar;
  }

  public static void read(Cellar cellar, Element element) {
    cellar.setName(element.getAttribute("name"));
    addElements(cellar, element);
  }

  public static CellarConstant readCellarConstant(ModelObjectFactory factory, Element element) {
    final var constant = factory.newCellarConstant();
    read(constant, element);
    return constant;
  }

  public static void read(CellarConstant constant, Element element) {
    constant.setFactory(element.getAttribute("factory"));
    constant.setSelector(element.getAttribute("selector"));
    constant.setName(element.getAttribute("name"));
    addElements(constant, element);
  }

  public static void read(Literal literal, Element element) {
    literal.setType(Literal.Type.valueOf(element.getAttribute("type").toUpperCase()));
    literal.setValue(element.getTextContent());
  }

  public static void read(ConstRef constRef, Element element) {
    constRef.setCellar(element.getAttribute("cellar"));
    constRef.setRef(element.getAttribute("ref"));
  }

  public static void read(Null nullConst, Element element) {
  }

  public static void read(Ref ref, Element element) {
    ref.setCellar(element.getAttribute("cellar"));
    ref.setRack(element.getAttribute("rack"));
    ref.setRef(element.getAttribute("ref"));
  }

  public static Rack readRack(ModelObjectFactory factory, Element element) {
    final var rack = factory.newRack();
    read(rack, element);
    return rack;
  }

  public static void read(Rack rack, Element element) {
    rack.setName(element.getAttribute("name"));
    rack.setFactory(element.getAttribute("factory"));
    addElements(rack, element);
  }

  public static void read(Initializer initializer, Element element) {
    initializer.setName(element.getAttribute("name"));
    addElements(initializer, element);
  }

  public static void readGeneric(Entity entity, Element element) {
    try {
      final var bh = LINKER.link(
        new SimpleRelinkableCallSite(
          new CallSiteDescriptor(
            MethodHandles.publicLookup(),
            StandardOperation.GET.withNamespace(StandardNamespace.METHOD).named("read"),
            MethodType.methodType(Object.class, StaticClass.class)
          )
        )
      ).dynamicInvoker().invoke(StaticClass.forClass(XmlModel.class));

      final var h = LINKER.link(
        new SimpleRelinkableCallSite(
          new CallSiteDescriptor(
            MethodHandles.publicLookup(),
            StandardOperation.CALL,
            MethodType.methodType(Object.class, Object.class, Object.class, Object.class, Object.class)
          )
        )
      );

      h.dynamicInvoker().invoke(bh, null, entity, element);
    } catch (Throwable e) {
      throw new IllegalStateException(e);
    }
  }

  public static void writeGeneric(Entity entity, Element element) {
    try {
      final var bh = LINKER.link(
        new SimpleRelinkableCallSite(
          new CallSiteDescriptor(
            MethodHandles.publicLookup(),
            StandardOperation.GET.withNamespace(StandardNamespace.METHOD).named("write"),
            MethodType.methodType(Object.class, StaticClass.class)
          )
        )
      ).dynamicInvoker().invoke(StaticClass.forClass(XmlModel.class));

      final var h = LINKER.link(
        new SimpleRelinkableCallSite(
          new CallSiteDescriptor(
            MethodHandles.publicLookup(),
            StandardOperation.CALL,
            MethodType.methodType(Object.class, Object.class, Object.class, Object.class, Object.class)
          )
        )
      );

      h.dynamicInvoker().invoke(bh, null, entity, element);
    } catch (Throwable e) {
      throw new IllegalStateException(e);
    }
  }

  private static Stream<Element> children(Element element) {
    final var list = element.getChildNodes();
    return IntStream.range(0, list.getLength())
      .mapToObj(list::item)
      .filter(Element.class::isInstance)
      .map(Element.class::cast);
  }

  private static void addElements(Entity entity, Element element) {
    try {
      final var list = element.getChildNodes();
      for (int i = 0; i < list.getLength(); i++) {
        final var node = list.item(i);
        if (node instanceof Element) {
          final var e = (Element) node;
          final var c = entity.modelObjectFactory().newEntity(e.getTagName());
          for (final var m : entity.getClass().getMethods()) {
            if (m.getName().startsWith("add") && m.getParameterCount() == 1 && m.getParameterTypes()[0].isAssignableFrom(c.getClass())) {
              m.invoke(entity, c);
              break;
            }
          }
          readGeneric(c, e);
        }
      }
    } catch (ReflectiveOperationException e) {
      throw new IllegalStateException(e);
    }
  }
}
