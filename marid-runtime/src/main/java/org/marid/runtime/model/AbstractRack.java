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

import org.marid.runtime.exception.RackCreationException;

import java.util.concurrent.Callable;

public abstract class AbstractRack<E> {

  private static final StackWalker STACK_WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

  public final Class<?> caller;
  protected final E instance;

  public AbstractRack(Callable<E> instanceSupplier) {
    this.caller = STACK_WALKER.getCallerClass();

    deployment().racks.add(this);

    try {
      this.instance = instanceSupplier.call();
    } catch (Throwable e) {
      throw new RackCreationException(caller, e);
    }
  }

  public void init(AutoCloseable initAction) {
    try {
      initAction.close();
      if (this instanceof Runnable) {
        ((Runnable) this).run();
      }
    } catch (Throwable e) {
      throw new RackCreationException(caller, e);
    }
  }

  protected Deployment deployment() {
    return classLoader().deployment;
  }

  protected RackClassLoader classLoader() {
    final var current = getClass().getClassLoader();
    if (current instanceof RackClassLoader) {
      return (RackClassLoader) current;
    } else {
      return (RackClassLoader) Thread.currentThread().getContextClassLoader();
    }
  }

  public final E get() {
    return instance;
  }
}
