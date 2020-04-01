package org.marid.model;

public interface ModelObjectFactory {

  ModelObjectFactoryImpl FACTORY = new ModelObjectFactoryImpl();

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
    return ModelObjectFactoryFriend.newEntity(this, tag);
  }
}