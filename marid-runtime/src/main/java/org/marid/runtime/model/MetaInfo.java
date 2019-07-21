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

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import static java.util.Arrays.stream;

public final class MetaInfo implements Serializable {

  public final String name;
  public final String title;
  public final String icon;
  public final String description;
  public final String author;
  public final String version;
  public final String modificationDate;

  public MetaInfo(String name,
                  String title,
                  String icon,
                  String description,
                  String author,
                  String version,
                  String modificationDate) {
    this.name = name;
    this.title = title;
    this.icon = icon;
    this.description = description;
    this.author = author;
    this.version = version;
    this.modificationDate = modificationDate;
  }

  public MetaInfo(String name, String modificationDate, MetaInfo... infos) {
    this(
        name,
        stream(infos).findFirst().map(e -> e.title).map(String::trim).filter(t -> !t.isEmpty()).orElse(name),
        stream(infos).map(e -> e.icon).map(String::trim).filter(i -> !i.isEmpty()).findFirst().orElse("runtime/defaultEntity.png"),
        stream(infos).findFirst().map(e -> e.description).map(String::trim).filter(d -> !d.isEmpty()).orElse(""),
        stream(infos).map(e -> e.author).map(String::trim).filter(i -> !i.isEmpty()).findFirst().orElse(""),
        stream(infos).map(e -> e.version).map(String::trim).filter(i -> !i.isEmpty()).findFirst().orElse("unknown"),
        stream(infos).map(e -> e.modificationDate).map(String::trim).filter(i -> !i.isEmpty()).findFirst().orElse(modificationDate)
    );
  }

  public MetaInfo(String name, String modificationDate, Info info) {
    this(
        name,
        Optional.ofNullable(info).map(Info::title).orElse(""),
        Optional.ofNullable(info).map(Info::icon).orElse(""),
        Optional.ofNullable(info).map(Info::description).orElse(""),
        Optional.ofNullable(info).map(Info::author).orElse(""),
        Optional.ofNullable(info).map(Info::version).orElse(""),
        modificationDate
    );
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, title, icon, description, author, version, modificationDate);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }

    if (!(obj instanceof MetaInfo)) {
      return false;
    }

    final var that = (MetaInfo) obj;

    return Arrays.equals(
        new String[]{this.name, this.title, this.icon, this.description, this.author, this.version, this.modificationDate},
        new String[]{that.name, that.title, that.icon, that.description, that.author, that.version, that.modificationDate}
    );
  }

  @Override
  public String toString() {
    return String.format("{name=%s,title=%s,icon=%s,description=%s,author=%s,version=%s,modificationDate=%s",
        name,
        title,
        icon,
        description,
        author,
        version,
        modificationDate
    );
  }
}
