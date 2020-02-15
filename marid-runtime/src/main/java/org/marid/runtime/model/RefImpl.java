package org.marid.runtime.model;

/*-
 * #%L
 * marid-runtime
 * %%
 * Copyright (C) 2012 - 2019 MARID software development group
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

public final class RefImpl extends AbstractArgument implements Ref {

  private String cellar;
  private String rack;
  private String ref;

  RefImpl() {}

  public RefImpl(String cellar, String rack, String ref) {
    this.cellar = cellar;
    this.rack = rack;
    this.ref = ref;
  }

  @Override public String getCellar() { return cellar; }
  @Override public void setCellar(String cellar) { this.cellar = cellar; }
  @Override public String getRack() { return rack; }
  @Override public void setRack(String rack) { this.rack = rack; }
  @Override public String getRef() { return ref; }
  @Override public void setRef(String ref) { this.ref = ref; }
}
