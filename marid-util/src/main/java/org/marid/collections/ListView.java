/*-
 * #%L
 * marid-util
 * %%
 * Copyright (C) 2012 - 2018 MARID software development group
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
package org.marid.collections;

import org.jetbrains.annotations.NotNull;
import org.marid.misc.Casts;

import java.util.*;
import java.util.function.Function;

public class ListView<E, V> implements List<V>, RandomAccess {

  private final List<E> delegate;
  private final Function<E, V> extractor;

  public <L extends List<E> & RandomAccess> ListView(L delegate, Function<E, V> extractor) {
    this.delegate = delegate;
    this.extractor = extractor;
  }

  @Override
  public int size() {
    return delegate.size();
  }

  @Override
  public boolean isEmpty() {
    return delegate.isEmpty();
  }

  @Override
  public boolean contains(Object o) {
    return delegate.stream().anyMatch(e -> Objects.equals(extractor.apply(e), o));
  }

  @NotNull
  @Override
  public Iterator<V> iterator() {
    final var iterator = delegate.iterator();
    return new Iterator<>() {
      @Override
      public boolean hasNext() {
        return iterator.hasNext();
      }

      @Override
      public V next() {
        return extractor.apply(iterator.next());
      }
    };
  }

  @NotNull
  @Override
  public Object[] toArray() {
    return delegate.stream().map(extractor).toArray();
  }

  @NotNull
  @Override
  public <T> T[] toArray(@NotNull T[] a) {
    return delegate.stream().map(extractor).toArray(size -> Arrays.copyOf(a, size));
  }

  @Override
  public boolean add(V v) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean remove(Object o) {
    return delegate.removeIf(e -> Objects.equals(extractor.apply(e), o));
  }

  @Override
  public boolean containsAll(@NotNull Collection<?> c) {
    return c.stream().allMatch(ce -> delegate.stream().anyMatch(e -> Objects.equals(extractor.apply(e), ce)));
  }

  @Override
  public boolean addAll(@NotNull Collection<? extends V> c) {
    if (c.isEmpty()) {
      return false;
    } else {
      throw new UnsupportedOperationException();
    }
  }

  @Override
  public boolean addAll(int index, @NotNull Collection<? extends V> c) {
    if (c.isEmpty()) {
      return false;
    } else {
      throw new UnsupportedOperationException();
    }
  }

  @Override
  public boolean removeAll(@NotNull Collection<?> c) {
    return delegate.removeIf(e -> c.contains(extractor.apply(e)));
  }

  @Override
  public boolean retainAll(@NotNull Collection<?> c) {
    return delegate.removeIf(e -> !c.contains(extractor.apply(e)));
  }

  @Override
  public void clear() {
    delegate.clear();
  }

  @Override
  public V get(int index) {
    final var e = delegate.get(index);
    return e == null ? null : extractor.apply(e);
  }

  @Override
  public V set(int index, V element) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void add(int index, V element) {
    throw new UnsupportedOperationException();
  }

  @Override
  public V remove(int index) {
    final var e = delegate.remove(index);
    return e == null ? null : extractor.apply(e);
  }

  @Override
  public int indexOf(Object o) {
    final V e = Casts.cast(o);
    for (final var i = delegate.listIterator(); i.hasNext(); ) {
      final var el = i.next();
      if (Objects.equals(extractor.apply(el), e)) {
        return i.previousIndex();
      }
    }
    return -delegate.size() - 1;
  }

  @Override
  public int lastIndexOf(Object o) {
    final V e = Casts.cast(o);
    for (int i = delegate.size() - 1; i >= 0; i--) {
      final var el = delegate.get(i);
      if (Objects.equals(extractor.apply(el), e)) {
        return i;
      }
    }
    return -delegate.size() - 1;
  }

  @NotNull
  @Override
  public ListIterator<V> listIterator() {
    return listIterator(0);
  }

  @NotNull
  @Override
  public ListIterator<V> listIterator(int index) {
    final var iterator = delegate.listIterator(index);
    return new ListIterator<>() {
      @Override
      public boolean hasNext() {
        return iterator.hasNext();
      }

      @Override
      public V next() {
        return extractor.apply(iterator.next());
      }

      @Override
      public boolean hasPrevious() {
        return iterator.hasPrevious();
      }

      @Override
      public V previous() {
        return extractor.apply(iterator.previous());
      }

      @Override
      public int nextIndex() {
        return iterator.nextIndex();
      }

      @Override
      public int previousIndex() {
        return iterator.previousIndex();
      }

      @Override
      public void remove() {
        iterator.remove();
      }

      @Override
      public void set(V v) {
        throw new UnsupportedOperationException();
      }

      @Override
      public void add(V v) {
        throw new UnsupportedOperationException();
      }
    };
  }

  @NotNull
  @Override
  public List<V> subList(int fromIndex, int toIndex) {
    return new ListView<>((List<E> & RandomAccess) delegate.subList(fromIndex, toIndex), extractor);
  }
}
