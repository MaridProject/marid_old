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

import org.marid.runtime.exception.CellarCloseException;
import org.marid.runtime.exception.CellarStartException;
import org.marid.runtime.model.Cellar;
import org.marid.runtime.model.CellarConstant;

import java.util.LinkedHashMap;
import java.util.LinkedList;

public class CellarRuntime implements AutoCloseable {

  public final String name;

  final WineryRuntime winery;
  final LinkedHashMap<String, RackRuntime> racks = new LinkedHashMap<>();
  final LinkedHashMap<String, Object> constants = new LinkedHashMap<>();

  CellarRuntime(WineryRuntime winery, String name, Cellar cellar) {
    this.winery = winery;
    this.name = name;

    try {
      for (final var rack: cellar.getRacks()) {
        this.racks.put(rack.getName(), new RackRuntime(this, rack));
      }
    } catch (Throwable e) {
      final var ex = new CellarStartException(this, e);
      try {
        close();
      } catch (Throwable x) {
        ex.addSuppressed(x);
      }
      throw ex;
    }
  }

  private Object constant(CellarConstant constant) throws Throwable {
    return null;
  }

  @Override
  public void close() {
    constants.clear();
    final var rackEntries = new LinkedList<>(racks.entrySet());
    racks.clear();
    final var ex = new CellarCloseException(this);
    for (final var it = rackEntries.descendingIterator(); it.hasNext(); ) {
      final var entry = it.next();
      final var rack = entry.getValue();
      try {
        rack.close();
      } catch (Throwable e) {
        ex.addSuppressed(e);
      } finally {
        it.remove();
      }
    }
    if (ex.getSuppressed().length > 0) {
      throw ex;
    }
  }
}
