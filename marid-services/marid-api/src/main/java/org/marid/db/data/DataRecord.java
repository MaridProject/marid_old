/*-
 * #%L
 * marid-api
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

package org.marid.db.data;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Arrays;

import static java.lang.String.format;

/**
 * @author Dmitry Ovchinnikov.
 */
public final class DataRecord<T extends Serializable> implements Serializable {

  private final long tag;
  private final long timestamp;
  private final T value;

  public DataRecord(long tag, long timestamp, @NotNull T value) {
    this.tag = tag;
    this.timestamp = timestamp;
    this.value = value;
  }

  public long getTag() {
    return tag;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public T getValue() {
    return value;
  }

  public DataRecordKey getKey() {
    return new DataRecordKey(tag, timestamp);
  }

  @Override
  public int hashCode() {
    return Arrays.deepHashCode(new Object[]{tag, timestamp, value});
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof DataRecord) {
      final DataRecord<?> that = (DataRecord<?>) obj;
      return Arrays.deepEquals(
          new Object[]{this.tag, this.timestamp, this.value},
          new Object[]{that.tag, that.timestamp, that.value});
    } else {
      return false;
    }
  }

  @Override
  public String toString() {
    String val = Arrays.deepToString(new Object[]{value});
    if (val.length() > 100) {
      val = val.substring(0, 100) + "...]";
    }
    return format("(%d, %tc, %s)", tag, timestamp, val);
  }
}
