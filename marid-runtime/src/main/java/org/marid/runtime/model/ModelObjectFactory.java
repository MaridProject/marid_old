package org.marid.runtime.model;

public interface ModelObjectFactory {

  Winery newWinery();

  Ref newRef();

  Rack newRack();

  Output newOutput();

  Null newNull();

  Literal newLiteral();

  Input newInput();

  Destroyer newDestroyer();

  ConstRef newConstRef();

  Cellar newCellar();

  CellarConstant newCellarConstant();
}
