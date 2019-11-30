package org.marid.runtime.internal;

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

import org.jetbrains.annotations.NotNull;
import org.marid.runtime.model.Cellar;
import org.marid.runtime.model.Rack;
import org.marid.runtime.model.Winery;

public class RackRuntime implements AutoCloseable {

  private final CellarRuntime cellar;
  private final Rack rack;
  final Object instance;

  RackRuntime(CellarRuntime cellar, Rack rack, Object instance) {
    this.cellar = cellar;
    this.rack = rack;
    this.instance = instance;
  }

  public @NotNull String getName() {
    return rack.getName();
  }

  public @NotNull Winery getWinery() {
    return cellar.winery.winery;
  }

  public @NotNull Cellar getCellar() {
    return cellar.cellar;
  }

  public @NotNull Rack getRack() {
    return rack;
  }

  public @NotNull Object getInstance() {
    return instance;
  }

  public @NotNull String getId() {
    return cellar.getId() + "/" + getName();
  }

  @Override
  public String toString() {
    return getId();
  }

  @Override
  public void close() {
    final var exception = new IllegalStateException("Unable to close rack " + cellar.winery.getId());
    if (instance instanceof AutoCloseable) {
      try {
        ((AutoCloseable) instance).close();
      } catch (Throwable e) {
        exception.addSuppressed(e);
      }
    }
    if (exception.getSuppressed().length > 0) {
      throw exception;
    }
  }
}
