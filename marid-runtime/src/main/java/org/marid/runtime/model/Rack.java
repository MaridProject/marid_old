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

public final class Rack<E> implements AutoCloseable {

  private static final StackWalker STACK_WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

  public final Class<?> caller;
  public final E instance;
  private final LinkedList<RackInstanceConsumer<E>> destroyers = new LinkedList<>();

  @SafeVarargs
  public Rack(RackInstanceSupplier<E> supplier, RackInstanceConsumer<E>... configurers) {
    this.caller = STACK_WALKER.getCallerClass();

    Deployment.getDeployment().racks.add(this);

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

  public Rack<E> withDestroyer(RackInstanceConsumer<E> destroyer) {
    destroyers.add(destroyer);
    return this;
  }

  @Override
  public void close() {
    if (instance != null) {
      final var exception = new RackCloseException(this);
      for (final var it = destroyers.iterator(); it.hasNext(); ) {
        final var destroyer = it.next();
        try {
          destroyer.accept(instance);
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
}
