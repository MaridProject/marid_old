/*-
 * #%L
 * marid-webapp
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
package org.marid.applib.dao;

import org.marid.applib.model.Elem;

import java.util.Comparator;

public class SortedListStore<I, T extends Elem<I>, D extends ListDao<I, T>> extends ListStore<I, T, D> {

  private final Comparator<? super I> comparator;

  public SortedListStore(D dao, Comparator<? super I> comparator) {
    super(dao);
    this.comparator = comparator;
  }

  @Override
  protected int locateIndex(I key) {
    int low = 0;
    int high = list.size() - 1;

    while (low <= high) {
      final int mid = (low + high) >>> 1;
      final T midVal = list.get(mid);
      final int cmp = comparator.compare(midVal.getId(), key);

      if (cmp < 0) {
        low = mid + 1;
      } else if (cmp > 0) {
        high = mid - 1;
      } else {
        return mid;
      }
    }

    return -(low + 1);
  }
}
