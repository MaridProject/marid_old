package org.marid.racks.collection;

/*-
 * #%L
 * marid-racks
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

import org.marid.runtime.annotation.Input;
import org.marid.runtime.annotation.Rack;
import org.marid.runtime.model.AbstractRack;

import java.util.ArrayList;
import java.util.Arrays;

@Rack(title = "Array list")
public class ArrayListRack<E> extends AbstractRack<ArrayList<E>> {

  @SafeVarargs
  public ArrayListRack(@Input(code = "+") E... element) {
    super(() -> new ArrayList<>(Arrays.asList(element)));
  }
}
