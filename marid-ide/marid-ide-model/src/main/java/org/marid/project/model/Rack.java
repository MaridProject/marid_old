package org.marid.project.model;

/*-
 * #%L
 * marid-ide-model
 * %%
 * Copyright (C) 2012 - 2019 MARID software development group
 * %%
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 * 
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 * #L%
 */

import org.w3c.dom.Element;
import org.xml.sax.InputSource;

public class Rack extends AbstractEntity {

  private final Cellar cellar;

  public Rack(Cellar cellar, String id, String name) {
    super(id, name);
    this.cellar = cellar;
  }

  Rack(Cellar cellar, Element element) {
    super(element);
    this.cellar = cellar;
  }

  Rack(Cellar cellar, InputSource source) {
    this(cellar, element(source));
  }

  public Cellar getCellar() {
    return cellar;
  }

  public Winery getWinery() {
    return cellar.getWinery();
  }

  @Override
  String tag() {
    return "rack";
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (obj instanceof Rack) {
      final var that = (Rack) obj;
      return true;
    }
    return false;
  }
}
