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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Cellar extends AbstractEntity {

  private final Winery winery;
  private final ArrayList<Rack> racks = new ArrayList<>();

  public Cellar(Winery winery, String id, String name) {
    super(id, name);
    this.winery = winery;
  }

  Cellar(Winery winery, Element element) {
    super(element);
    this.winery = winery;
    final var racks = element.getElementsByTagName("rack");
    for (int i = 0; i < racks.getLength(); i++) {
      final var e = (Element) racks.item(i);
      this.racks.add(new Rack(this, e));
    }
  }

  Cellar(Winery winery, InputSource source) {
    this(winery, element(source));
  }

  public Rack rack(InputSource source) {
    return new Rack(this, element(source));
  }

  public Winery getWinery() {
    return winery;
  }

  public List<Rack> getRacks() {
    return racks;
  }

  @Override
  void save(Element element) {
    super.save(element);
    for (final var rack : racks) {
      final var e = element.getOwnerDocument().createElement(rack.tag());
      element.appendChild(e);
      rack.save(e);
    }
  }

  @Override
  String tag() {
    return "cellar";
  }

  @Override
  public int hashCode() {
    return super.hashCode() ^ Objects.hashCode(racks);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (obj instanceof Cellar) {
      final var that = (Cellar) obj;

      return Objects.equals(this.racks, that.racks);
    }
    return false;
  }
}
