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

public final class Link extends HtmlChild implements HtmlBase<Link> {

  public Link(HasNode<?> node) {
    super(node.getNode(), "link");
  }

  public Link rel(@NotNull String rel, @NotNull String href) {
    getNode().setAttribute("rel", rel);
    getNode().setAttribute("href", href);
    return this;
  }

  public Link icon(@NotNull String href) {
    return rel("icon", href);
  }

  public Link stylesheet(@NotNull String href) {
    return rel("stylesheet", href);
  }

  @Override
  public Link getSelf() {
    return this;
  }
}
