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

import org.marid.runtime.exception.ContextCloseException;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public final class Context implements AutoCloseable {

  private final List<String> args;
  final LinkedHashMap<Class<? extends Rack>, Rack> racks = new LinkedHashMap<>();

  public Context(List<String> args) {
    this.args = args;
  }

  public List<String> getArgs() {
    return args;
  }

  public <R extends Rack> R getRack(Class<R> type) {
    return type.cast(racks.get(type));
  }

  @Override
  public void close() throws Exception {
    final ContextCloseException exception = new ContextCloseException();

    final var keys = new LinkedList<>(racks.keySet());
    for (final var it = keys.descendingIterator(); it.hasNext(); ) {
      final var key = it.next();
      try {
        racks.remove(key).close();
      } catch (Throwable e) {
        exception.addSuppressed(e);
      } finally {
        it.remove();
      }
    }

    if (exception.getSuppressed().length > 0) {
      throw exception;
    }
  }
}
