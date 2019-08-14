package org.marid.project.model;

public class Cellar extends AbstractEntity {

  public final Winery winery;

  public Cellar(Winery winery) {
    this.winery = winery;
  }
}
