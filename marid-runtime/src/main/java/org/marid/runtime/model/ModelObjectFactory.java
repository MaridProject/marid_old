package org.marid.runtime.model;

public interface ModelObjectFactory {

  Winery newWinery();

  Ref newRef();

  Rack newRack();

  Output newOutput();

  Null newNull();

  Literal newLiteral();

  ConstRef newConstRef();

  Cellar newCellar();

  Initializer newInitializer();

  CellarConstant newCellarConstant();

  default Entity newEntity(String tag) {
    try {
      for (final var m : ModelObjectFactory.class.getMethods()) {
        if (m.getParameterCount() == 0 && Entity.class.isAssignableFrom(m.getReturnType())) {
          final var instance = (Entity) m.invoke(this);
          if (instance.tag().equals(tag)) {
            return instance;
          }
        }
      }
      throw new IllegalArgumentException("Unable to find entity by tag: " + tag);
    } catch (ReflectiveOperationException e) {
      throw new IllegalStateException(e);
    }
  }
}
