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

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import java.util.*;

public class Winery extends AbstractEntity {

  private final List<Cellar> cellars = new ArrayList<>();

  public Winery(@NotNull String id, @NotNull String name) {
    super(id, name);
  }

  public Winery(Element element) {
    super(element);
    final var cellars = element.getElementsByTagName("cellar");
    for (int i = 0; i < cellars.getLength(); i++) {
      final var e = (Element) cellars.item(i);
      this.cellars.add(new Cellar(this, e));
    }
  }

  public Winery(InputSource inputSource) {
    this(element(inputSource));
  }

  public Cellar cellar(Element element) {
    return new Cellar(this, element);
  }

  public Cellar cellar(InputSource source) {
    return new Cellar(this, element(source));
  }

  public List<Cellar> getCellars() {
    return cellars;
  }

  @Override
  void save(Element element) {
    super.save(element);
    for (final var cellar : cellars) {
      final var e = element.getOwnerDocument().createElement(cellar.tag());
      element.appendChild(e);
      cellar.save(e);
    }
  }

  @Override
  String tag() {
    return "winery";
  }

  @Override
  public int hashCode() {
    return super.hashCode() ^ Objects.hashCode(cellars);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (obj instanceof Winery) {
      final var that = (Winery) obj;

      return Objects.equals(this.cellars, that.cellars);
    }
    return false;
  }
}
