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

import org.marid.runtime.exception.RackCloseException;
import org.marid.runtime.exception.RackCreationException;

import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class Rack<E> implements AutoCloseable {

  private static final StackWalker STACK_WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

  public final Class<?> caller;
  public final E instance;
  private final LinkedList<Consumer<E>> destroyers = new LinkedList<>();

  @SafeVarargs
  public Rack(Supplier<E> supplier, Consumer<E>... configurers) {
    this.caller = STACK_WALKER.getCallerClass();

    try {
      this.instance = supplier.get();
    } catch (Throwable e) {
      throw new RackCreationException(caller, e);
    }

    try {
      for (final var configurer : configurers) {
        configurer.accept(instance);
      }
    } catch (Throwable e) {
      try {
        close();
      } catch (Throwable ce) {
        e.addSuppressed(ce);
      }
      throw new RackCreationException(caller, e);
    }
  }

  public Rack<E> withDestroyer(Consumer<E> destroyer) {
    destroyers.add(destroyer);
    return this;
  }

  @Override
  public void close() {
    final var exception = new RackCloseException(this);

    destroyers.removeIf(d -> {
      try {
        d.accept(instance);
      } catch (Throwable e) {
        exception.addSuppressed(e);
      }
      return true;
    });

    if (exception.getSuppressed().length > 0) {
      throw exception;
    }
  }
}
