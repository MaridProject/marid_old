package org.marid.html;

/*-
 * #%L
 * marid-html
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

import org.jetbrains.annotations.NotNull;

import java.util.Enumeration;
import java.util.function.Consumer;
import java.util.stream.Stream;

public interface HtmlBase<E extends HtmlElement> extends HasSelf<E> {

  default E id(@NotNull String id) {
    final var self = getSelf();
    self.getNode().setAttribute("id", id);
    return self;
  }

  default E klass(@NotNull String klass) {
    final var self = getSelf();
    self.getNode().setAttribute("class", klass);
    return self;
  }

  default E style(@NotNull String style) {
    final var self = getSelf();
    self.getNode().setAttribute("style", style);
    return self;
  }

  default <T> E forEach(@NotNull Stream<T> stream, @NotNull Consumer<T> consumer) {
    stream.forEach(consumer);
    return getSelf();
  }

  default <T> E forEach(@NotNull Iterable<T> collection, @NotNull Consumer<T> consumer) {
    collection.forEach(consumer);
    return getSelf();
  }

  default <T> E forEach(@NotNull Enumeration<T> enumeration, @NotNull Consumer<T> consumer) {
    while (enumeration.hasMoreElements()) {
      consumer.accept(enumeration.nextElement());
    }
    return getSelf();
  }
}
