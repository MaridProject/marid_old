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
package org.marid.applib.dao;

import org.marid.applib.model.Identifiable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

import static java.util.Collections.emptyList;
import static org.marid.applib.dao.ListManager.EventType.*;

public class ListManager<I, T extends Identifiable<I>, D extends ListDao<I, T>> {

  protected final D dao;
  protected final Logger logger;
  protected final ArrayList<T> list = new ArrayList<>();
  protected final EnumMap<EventType, Collection<Consumer<Event>>> listeners = new EnumMap<>(EventType.class);

  public ListManager(D dao) {
    this.dao = dao;
    this.logger = LoggerFactory.getLogger(getClass());
  }

  public void refresh() {
    final var newList = dao.get();
    final var remove = new TreeMap<Integer, T>();

    for (int i = list.size() - 1; i >= 0; i--) {
      final var e = list.get(i);
      if (newList.stream().noneMatch(v -> v.getId().equals(e.getId()))) {
        list.remove(i);
        remove.put(i, e);
      }
    }

    if (!remove.isEmpty()) {
      listeners.getOrDefault(REMOVE, emptyList()).forEach(new Event(remove)::fire);
    }

    add(newList);
  }

  protected int locateIndex(I key) {
    for (final var i = list.listIterator(); i.hasNext(); ) {
      final var e = i.next();
      if (e.getId().equals(key)) {
        return i.previousIndex();
      }
    }
    return -list.size() - 1;
  }

  public void add(List<T> added) {
    final var add = new TreeMap<Integer, T>();
    final var update = new TreeMap<Integer, T>();
    for (final var e : added) {
      final int index = locateIndex(e.getId());
      if (index < 0) {
        final int pos = -(index + 1);
        add.put(pos, e);
        list.add(pos, e);
        dao.add(e);
      } else if (!e.equals(list.get(index))) {
        update.put(index, e);
        dao.update(e);
      }
    }
    if (!add.isEmpty()) {
      listeners.getOrDefault(ADD, emptyList()).forEach(new Event(add)::fire);
    }

    if (!update.isEmpty()) {
      listeners.getOrDefault(UPDATE, emptyList()).forEach(new Event(update)::fire);
    }
  }

  public void remove(List<I> removed) {
    final var remove = new TreeMap<Integer, T>();
    for (final var id : removed) {
      final int index = locateIndex(id);
      if (index >= 0) {
        final var e = get(index);
        remove.put(index, e);
        dao.remove(e);
      }
    }
    if (!remove.isEmpty()) {
      remove.descendingKeySet().stream().mapToInt(Integer::intValue).forEach(list::remove);
      listeners.getOrDefault(REMOVE, emptyList()).forEach(new Event(remove)::fire);
    }
  }

  public void remove(int... indices) {
    final TreeMap<Integer, T> remove = new TreeMap<>();
    for (final int i : indices) {
      if (i >= 0 && i < list.size()) {
        final var e = list.get(i);
        remove.put(i, e);
        dao.remove(e);
      }
    }
    if (!remove.isEmpty()) {
      remove.descendingKeySet().stream().mapToInt(Integer::intValue).forEach(list::remove);
      listeners.getOrDefault(REMOVE, emptyList()).forEach(new Event(remove)::fire);
    }
  }

  public Consumer<Event> addListener(EventType type, Consumer<Event> listener) {
    listeners.computeIfAbsent(type, k -> new ConcurrentLinkedQueue<>()).add(listener);
    return listener;
  }

  public void removeListener(EventType type, Consumer<Event> listener) {
    listeners.computeIfPresent(type, (k, old) -> old.remove(listener) && old.isEmpty() ? null : old);
  }

  public T get(int index) {
    return list.get(index);
  }

  public class Event {

    public final TreeMap<Integer, T> update;

    public Event(TreeMap<Integer, T> update) {
      this.update = update;
    }

    public ListManager<I, T, D> getSource() {
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

  public enum EventType {
    ADD,
    REMOVE,
    UPDATE
  }
}
