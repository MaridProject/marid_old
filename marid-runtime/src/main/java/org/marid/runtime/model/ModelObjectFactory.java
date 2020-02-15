package org.marid.runtime.model;

public interface ModelObjectFactory {

  Winery newWinery();

  Ref newRef();

  Rack newRack();

  Output newOutput();

  Null newNull();

  Literal newLiteral();

  Input newInput();

  ConstRef newConstRef();

  Cellar newCellar();

  Initializer newInitializer();

  CellarConstant newCellarConstant();
}
