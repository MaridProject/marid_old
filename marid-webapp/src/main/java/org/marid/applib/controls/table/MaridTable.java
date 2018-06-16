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
package org.marid.applib.controls.table;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import java.util.Collection;
import java.util.EnumMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

import static java.util.Collections.emptyList;
import static java.util.stream.IntStream.of;
import static java.util.stream.IntStream.rangeClosed;
import static org.marid.applib.controls.table.MaridTable.EventType.ADD;
import static org.marid.applib.controls.table.MaridTable.EventType.REMOVE;

public class MaridTable extends Table {

  private final EnumMap<EventType, Collection<Consumer<Event>>> listeners = new EnumMap<>(EventType.class);

  public MaridTable(Composite parent, int style) {
    super(parent, style);
  }

  @Override
  public void remove(int start, int end) {
    super.remove(start, end);
    listeners.getOrDefault(REMOVE, emptyList()).forEach(new Event(rangeClosed(start, end).toArray())::fire);
  }

  @Override
  public void remove(int index) {
    super.remove(index);
    listeners.getOrDefault(REMOVE, emptyList()).forEach(new Event(index)::fire);
  }

  @Override
  public void remove(int[] indices) {
    super.remove(indices);
    if (indices.length > 0) {
      listeners.getOrDefault(REMOVE, emptyList()).forEach(new Event(of(indices).sorted().toArray())::fire);
    }
  }

  @Override
  public void removeAll() {
    if (getItemCount() > 0) {
      remove(0, getItemCount() - 1);
    }
  }

  public Consumer<Event> addListener(EventType eventType, Consumer<Event> listener) {
    listeners.computeIfAbsent(eventType, k -> new ConcurrentLinkedQueue<>()).add(listener);
    return listener;
  }

  public void removeListener(EventType eventType, Consumer<Event> listener) {
    listeners.computeIfPresent(eventType, (k, old) -> old.remove(listener) && old.isEmpty() ? null : old);
  }

  public class Item extends TableItem {

    public Item(int style, int index) {
      super(MaridTable.this, style, index);
      listeners.getOrDefault(ADD, emptyList()).forEach(new Event(index)::fire);
    }

    public Item(int style) {
      this(style, MaridTable.this.getItemCount());
    }

    public MaridTable getTable() {
      return MaridTable.this;
    }

    public void setTexts(String... values) {
      setText(values);
    }

    public void setImages(Image... images) {
      setImage(images);
    }
  }

  public enum EventType {
    ADD,
    REMOVE
  }

  public class Event {

    private final int[] indices;

    private Event(int... indices) {
      this.indices = indices;
    }

    public int[] getIndices() {
      return indices;
    }

    private void fire(Consumer<Event> consumer) {
      consumer.accept(this);
    }
  }
}
