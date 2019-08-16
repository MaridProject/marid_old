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

import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Winery extends AbstractEntity {

  @JsonManagedReference
  List<Cellar> cellars = new ArrayList<>();

  public List<Cellar> getCellars() {
    return cellars;
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
