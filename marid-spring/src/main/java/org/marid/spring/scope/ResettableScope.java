package org.marid.spring.scope;

/*-
 * #%L
 * marid-spring
 * %%
 * Copyright (C) 2012 - 2020 MARID software development group
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
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ResettableScope implements Scope {

  private final LinkedHashMap<String, Object> beans = new LinkedHashMap<>();
  private final HashMap<String, LinkedList<Runnable>> callbacks = new HashMap<>();
  private final String id;
  private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

  public ResettableScope(String id) {
    this.id = id;
  }

  @NotNull
  @Override
  public Object get(@NotNull String name, @NotNull ObjectFactory<?> objectFactory) {
    lock.readLock().lock();
    try {
      var ref = beans.get(name);
      if (ref == null) {
        beans.put(name, ref = objectFactory.getObject());
      }
      return ref;
    } finally {
      lock.readLock().unlock();
    }
  }

  @Override
  public Object remove(@NotNull String name) {
    lock.readLock().lock();
    try {
      return beans.remove(name);
    } finally {
      lock.readLock().unlock();
    }
  }

  @Override
  public void registerDestructionCallback(@NotNull String name, @NotNull Runnable callback) {
    lock.readLock().lock();
    try {
      this.callbacks.computeIfAbsent(name, n -> new LinkedList<>()).add(callback);
    } finally {
      lock.readLock().unlock();
    }
  }

  @Override
  public Object resolveContextualObject(@NotNull String key) {
    return null;
  }

  @Override
  public String getConversationId() {
    return id;
  }

  public void reset() throws ScopeResetException {
    final var exception = new ScopeResetException(this);

    lock.writeLock().lock();
    try {
      final var keys = new LinkedList<>(beans.keySet());
      keys.descendingIterator().forEachRemaining(key -> {
        final var callbacks = this.callbacks.remove(key);
        if (callbacks == null) {
          beans.remove(key);
          return;
        }
        try {
          for (final var it = callbacks.descendingIterator(); it.hasNext(); ) {
            final var callback = it.next();
            try {
              callback.run();
            } catch (Throwable e) {
              exception.addSuppressed(e);
            } finally {
              it.remove();
            }
          }
        } finally {
          beans.remove(key);
        }
      });
    } finally {
      lock.writeLock().unlock();
    }

    if (exception.getSuppressed().length > 0) {
      throw exception;
    }
  }

  @Override
  public String toString() {
    return id;
  }
}
