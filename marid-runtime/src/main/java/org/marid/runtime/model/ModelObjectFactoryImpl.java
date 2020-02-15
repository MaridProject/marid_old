package org.marid.runtime.model;

public final class ModelObjectFactoryImpl implements ModelObjectFactory {
  @Override public Winery newWinery() { return new WineryImpl(); }
  @Override public Ref newRef() { return new RefImpl(); }
  @Override public Rack newRack() { return new RackImpl(); }
  @Override public Output newOutput() { return new OutputImpl(); }
  @Override public Null newNull() { return NullImpl.INSTANCE; }
  @Override public Literal newLiteral() { return new LiteralImpl(); }
  @Override public Input newInput() { return new InputImpl(); }
  @Override public ConstRef newConstRef() { return new ConstRefImpl(); }
  @Override public Cellar newCellar() { return new CellarImpl(); }
  @Override public Initializer newInitializer() { return new InitializerImpl(); }
  @Override public CellarConstant newCellarConstant() { return new CellarConstantImpl(); }
}
