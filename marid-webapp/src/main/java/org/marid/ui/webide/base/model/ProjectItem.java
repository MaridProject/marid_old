/*-
 * #%L
 * marid-webapp
 * %%
 * Copyright (C) 2012 - 2018 MARID software development group
 * %%
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * #L%
 */
package org.marid.ui.webide.base.model;

import org.jetbrains.annotations.NotNull;
import org.marid.applib.model.Identifiable;

public final class ProjectItem implements Identifiable<String> {

  public final String name;
  public final long size;

  public ProjectItem(String name, long size) {
    this.name = name;
    this.size = size;
  }

  @NotNull
  @Override
  public String getId() {
    return name;
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    } else if (obj == null || obj.getClass() != ProjectItem.class) {
      return false;
    } else {
      final var that = (ProjectItem) obj;
      return name.equals(that.name) && size == that.size;
    }
  }
}
