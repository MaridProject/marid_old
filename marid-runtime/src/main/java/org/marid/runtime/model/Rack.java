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

import org.marid.runtime.annotation.Destructor;
import org.marid.runtime.exception.RackCloseException;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Comparator;

public abstract class Rack<E> implements AutoCloseable {

  private final Context context;

  protected Rack(Context context) {
    this.context = context;
  }

  public abstract E get();

  @Override
  public void close() throws Exception {
    final var exception = new RackCloseException(this);

    Arrays.stream(getClass().getMethods())
        .filter(m -> m.isAnnotationPresent(Destructor.class))
        .filter(AccessibleObject::trySetAccessible)
        .sorted(Comparator.comparingInt(m -> m.getAnnotation(Destructor.class).order()))
        .forEachOrdered(m -> {
          final var argTypes = m.getParameterTypes();
          try {
            final var args = new Object[argTypes.length];
            for (int i = 0; i < args.length; i++) {
              final var arg = context.getRack(argTypes[i].asSubclass(Rack.class));
              args[i] = arg;
            }
            m.invoke(this, args);
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
