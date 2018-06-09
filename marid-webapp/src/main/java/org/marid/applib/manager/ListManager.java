/*-
 * #%L
 * marid-webapp
 * %%
 * Copyright (C) 2012 - 2018 MARID software development group
 * %%
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * #L%
 */
package org.marid.applib.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ListManager<D extends Supplier<List<T>>, T extends Comparable<? super T>> {

  protected final D dao;
  protected final Logger logger;
  protected final ArrayList<T> list = new ArrayList<>();
  protected final ConcurrentLinkedQueue<Consumer<Event>> addedListeners = new ConcurrentLinkedQueue<>();
  protected final ConcurrentLinkedQueue<Consumer<Event>> removedListeners = new ConcurrentLinkedQueue<>();
  protected final ConcurrentLinkedQueue<Consumer<Event>> updatedListeners = new ConcurrentLinkedQueue<>();

  public ListManager(D dao) {
    this.dao = dao;
    this.logger = LoggerFactory.getLogger(getClass());
  }

  public void refresh() {
    if (!list.isEmpty()) {
      final var m = new TreeMap<Integer, T>();
      for (int i = 0; i < list.size(); i++) {
        m.put(i, list.get(i));
      }
      removedListeners.forEach(new Event(m)::fire);
    }

    try {
      final var newList = dao.get();
      final var remove = new TreeMap<Integer, T>();

      for (int i = list.size() - 1; i >= 0; i--) {
        final var e = list.get(i);
        if (!newList.contains(e)) {
          list.remove(i);
          remove.put(i, e);
        }
      }

      if (!remove.isEmpty()) {
        removedListeners.forEach(new Event(remove)::fire);
      }

      add(newList);
    } catch (Exception x) {
      logger.warn("Unable to get data", x);
    }
  }

  public void add(List<T> added) {
    final var add = new TreeMap<Integer, T>();
    final var update = new TreeMap<Integer, T>();
    for (final var e : added) {
      final int index = Collections.binarySearch(list, e);
      if (index < 0) {
        final int pos = -(index + 1);
        add.put(pos, e);
      } else {
        update.put(index, e);
      }
    }
    if (!add.isEmpty()) {
      addedListeners.forEach(new Event(add)::fire);
    }

    if (!update.isEmpty()) {
      updatedListeners.forEach(new Event(update)::fire);
    }
  }

  public void remove(List<T> removed) {
    final var remove = new TreeMap<Integer, T>();
    for (final var e : removed) {
      final int index = Collections.binarySearch(list, e);
      if (index >= 0) {
        remove.put(index, e);
      }
    }
    if (!remove.isEmpty()) {
      remove.descendingKeySet().stream().mapToInt(Integer::intValue).forEach(list::remove);
      removedListeners.forEach(new Event(remove)::fire);
    }
  }

  public void remove(int... indices) {
    final TreeMap<Integer, T> remove = new TreeMap<>();
    for (final int i : indices) {
      if (i >= 0 && i < list.size()) {
        remove.put(i, list.get(i));
      }
    }
    if (!remove.isEmpty()) {
      remove.descendingKeySet().stream().mapToInt(Integer::intValue).forEach(list::remove);
      removedListeners.forEach(new Event(remove)::fire);
    }
  }

  public void update(List<T> updated) {
    final var update = new TreeMap<Integer, T>();
    for (final var e : updated) {
      final int index = Collections.binarySearch(list, e);
      if (index >= 0) {
        list.set(index, e);
        update.put(index, e);
      }
    }
    if (!update.isEmpty()) {
      updatedListeners.forEach(new Event(update)::fire);
    }
  }

  public Consumer<Event> addAddListener(Consumer<Event> listener) {
    addedListeners.add(listener);
    return listener;
  }

  public void removeAddListener(Consumer<Event> listener) {
    addedListeners.remove(listener);
  }

  public Consumer<Event> addUpdateListener(Consumer<Event> listener) {
    updatedListeners.add(listener);
    return listener;
  }

  public void removeUpdateListener(Consumer<Event> listener) {
    updatedListeners.remove(listener);
  }

  public Consumer<Event> addRemoveListener(Consumer<Event> listener) {
    removedListeners.add(listener);
    return listener;
  }

  public void removeRemoveListener(Consumer<Event> listener) {
    removedListeners.remove(listener);
  }

  public T get(int index) {
    return list.get(index);
  }

  public class Event {

    public final TreeMap<Integer, T> update;

    public Event(TreeMap<Integer, T> update) {
      this.update = update;
    }

    public ListManager<D, T> getSource() {
      return ListManager.this;
    }

    public void fire(Consumer<Event> listener) {
      try {
        listener.accept(this);
      } catch (Exception x) {
        logger.warn("Unable to invoke listener of {}", this, x);
      } catch (Throwable x) {
        logger.error("Unexpected error of {}", this, x);
      }
    }
  }
}
