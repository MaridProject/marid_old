package org.marid.runtime;

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

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.concurrent.Callable;

public abstract class AbstractRack<E> {

  public final Class<? extends AbstractCellar> caller;
  protected final E instance;

  public AbstractRack(Callable<E> instanceSupplier) {
    caller = Deployment.STACK_WALKER.getCallerClass().asSubclass(AbstractCellar.class);

    final AbstractCellar cellar;
    try {
      final var handle = MethodHandles.publicLookup().findStatic(caller, "provider", MethodType.methodType(caller));
      cellar = (AbstractCellar) handle.invokeExact(); // should be a singleton instance
    } catch (Throwable e) {
      throw new IllegalStateException("Unable to find a 'provider' method of " + caller);
    }
    try {
      instance = instanceSupplier.call();
    } catch (Throwable e) {
      try {
        cellar.close();
      } catch (Throwable ce) {
        e.addSuppressed(ce);
      }
      throw new RackCreationException(caller, e);
    }
  }

  public final E get() {
    return instance;
  }
}
