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

import java.security.PrivilegedExceptionAction;

public abstract class AbstractRack<E> {

  private static final StackWalker STACK_WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

  public final Class<?> caller;
  protected final E instance;

  public AbstractRack(PrivilegedExceptionAction<E> instanceSupplier) {
    this.caller = STACK_WALKER.getCallerClass();

    Deployment.getDeployment().racks.add(this);

    try {
      this.instance = instanceSupplier.run();
    } catch (Throwable e) {
      throw new RackCreationException(caller, e);
    }
  }

  public final E get() {
    return instance;
  }
}
