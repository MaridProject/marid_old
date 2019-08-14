package org.marid.project.model;

public class Rack extends AbstractEntity {

  public final Cellar cellar;

  public Rack(Cellar cellar) {
    this.cellar = cellar;
  }
}
