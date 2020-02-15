package org.marid.runtime.model;

import java.util.List;

public interface Cellar extends Entity {

  String getName();

  void setName(String name);

  List<? extends CellarConstant> getConstants();

  List<? extends Rack> getRacks();

  void addRack(Rack rack);

  void addConstant(CellarConstant constant);
}
