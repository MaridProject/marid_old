package org.marid.runtime.model;

public interface Ref extends Argument {

  String getCellar();

  void setCellar(String cellar);

  String getRack();

  void setRack(String rack);

  String getRef();

  void setRef(String ref);
}
