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

import org.marid.runtime.annotation.Destroy;
import org.marid.runtime.annotation.Initialize;
import org.marid.runtime.exception.RackCloseException;
import org.marid.runtime.exception.RackCreationException;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Comparator;

public abstract class AbstractRack<E> implements AutoCloseable {

  private static final StackWalker STACK_WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

  public final Class<?> caller;
  public final E instance;

  public AbstractRack(RackInstanceSupplier<E> instanceSupplier) {
    this.caller = STACK_WALKER.getCallerClass();

    Deployment.getDeployment().racks.add(this);

    try {
      this.instance = instanceSupplier.get();
    } catch (Throwable e) {
      throw new RackCreationException(caller, e);
    }

    try {
      Arrays.stream(getClass().getMethods())
          .filter(m -> m.getParameterCount() == 0)
          .filter(m -> m.isAnnotationPresent(Initialize.class))
          .filter(AccessibleObject::trySetAccessible)
          .sorted(Comparator.comparingInt(m -> m.getAnnotation(Initialize.class).order()))
          .forEachOrdered(method -> {
            try {
              method.invoke(this);
            } catch (InvocationTargetException e) {
              throw new RackCreationException(caller, e.getTargetException());
            } catch (Throwable e) {
              throw new RackCreationException(caller, e);
            }
          });
    } catch (Throwable e) {
      try {
        close();
      } catch (Throwable ce) {
        e.addSuppressed(ce);
      }
      throw e;
    }
  }

  @Override
  public void close() {
    if (instance != null) {
      final var exception = new RackCloseException(this);

      Arrays.stream(getClass().getMethods())
          .filter(m -> m.getParameterCount() == 0)
          .filter(m -> m.isAnnotationPresent(Destroy.class))
          .filter(AccessibleObject::trySetAccessible)
          .sorted(Comparator.comparingInt(m -> m.getAnnotation(Destroy.class).order()))
          .forEachOrdered(method -> {
            try {
              method.invoke(this);
            } catch (InvocationTargetException e) {
              exception.addSuppressed(e.getTargetException());
            } catch (Throwable e) {
              exception.addSuppressed(e);
            }
          });

      if (exception.getSuppressed().length > 0) {
        throw exception;
      }
    }
  }
}
