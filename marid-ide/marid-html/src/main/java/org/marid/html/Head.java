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

import java.util.function.Consumer;

public final class Head extends HtmlChild {

  public Head(HasNode<?> node) {
    super(node.getNode(), "head");
  }

  public Head title(@NotNull String title) {
    final var e = getNode().getOwnerDocument().createElement("title");
    e.setTextContent(title);
    getNode().appendChild(e);
    return this;
  }

  public Head title(Consumer<Title> titleConsumer) {
    final var title = new Title(this);
    titleConsumer.accept(title);
    return this;
  }

  public Head utf8() {
    final var e = getNode().getOwnerDocument().createElement("meta");
    e.setAttribute("charset", "utf-8");
    getNode().appendChild(e);
    return this;
  }

  public Head meta(Consumer<Meta> metaConsumer) {
    final var meta = new Meta(this);
    metaConsumer.accept(meta);
    return this;
  }

  public Head link(@NotNull String rel, @NotNull String href) {
    final var e = getNode().getOwnerDocument().createElement("link");
    e.setAttribute("rel", rel);
    e.setAttribute("href", href);
    getNode().appendChild(e);
    return this;
  }

  public Head icon(@NotNull String href) {
    return link("icon", href);
  }

  public Head stylesheet(@NotNull String href) {
    return link("stylesheet", href);
  }

  public Head script(@NotNull String src) {
    final var e = getNode().getOwnerDocument().createElement("script");
    e.setAttribute("src", src);
    getNode().appendChild(e);
    return this;
  }

  public Head script(@NotNull Consumer<Script> scriptConsumer) {
    final var script = new Script(this);
    scriptConsumer.accept(script);
    return this;
  }
}
