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

import org.marid.runtime.annotations.Info;

import java.util.LinkedHashMap;

public final class Cellar implements HasMetaInfo {

  private final Winery winery;
  private final Class<?> type;
  private final MetaInfo metaInfo;
  private final LinkedHashMap<String, Rack> racks = new LinkedHashMap<>();

  public Cellar(Winery winery, Class<?> type) {
    this.winery = winery;
    this.type = type;
    this.metaInfo = new MetaInfo(type.getSimpleName(), "",
        new MetaInfo("", "", type.getAnnotation(Info.class)),
        winery.getMetaInfo(),
        winery.getBusiness().getMetaInfo()
    );
  }

  public Winery getWinery() {
    return winery;
  }

  @Override
  public MetaInfo getMetaInfo() {
    return metaInfo;
  }

  public LinkedHashMap<String, Rack> getRacks() {
    return racks;
  }

  @Override
  public String toString() {
    return winery.toString() + "." + type.getName();
  }
}
