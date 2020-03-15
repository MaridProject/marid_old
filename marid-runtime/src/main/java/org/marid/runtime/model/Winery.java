package org.marid.runtime.model;

import java.util.List;

public interface Winery extends Entity {

  String getGroup();

  void setGroup(String group);

  String getName();

  void setName(String name);

  String getVersion();

  void setVersion(String version);

  List<? extends Cellar> getCellars();

  void addCellar(Cellar cellar);

  @Override default String tag() {return "winery";}
}
