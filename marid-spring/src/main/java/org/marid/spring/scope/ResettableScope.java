package org.marid.spring.scope;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.lang.NonNull;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static java.lang.System.Logger.Level.INFO;

public class ResettableScope implements Scope {

  private final LinkedHashMap<String, Object> beans = new LinkedHashMap<>();
  private final HashMap<String, LinkedList<Runnable>> callbacks = new HashMap<>();
  private final String id;
  private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
  private final System.Logger logger;

  public ResettableScope(String id) {
    this.id = id;
    this.logger = System.getLogger(id);
  }

  @NonNull
  @Override
  public Object get(@NonNull String name,@NonNull ObjectFactory<?> objectFactory) {
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
  public Object remove(@NonNull String name) {
    lock.readLock().lock();
    try {
      return beans.remove(name);
    } finally {
      lock.readLock().unlock();
    }
  }

  @Override
  public void registerDestructionCallback(@NonNull String name, @NonNull Runnable callback) {
    lock.readLock().lock();
    try {
      this.callbacks.computeIfAbsent(name, n -> new LinkedList<>()).add(callback);
    } finally {
      lock.readLock().unlock();
    }
  }

  @Override
  public Object resolveContextualObject(@NonNull String key) {
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
              logger.log(INFO, "Closing {0}", key);
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
