package org.marid.runtime.model;

public interface ConstRef extends ConstantArgument {

  String getCellar();

  void setCellar(String cellar);

  String getRef();

  void setRef(String ref);

  @Override default String tag() {return "const-ref";}
}
