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

import java.util.function.Consumer;

public interface HtmlContainer<E extends HtmlElement> extends HtmlBase<E>, HasSelf<E> {

  default E div(Consumer<Div> divConsumer) {
    final var div = new Div(getSelf());
    divConsumer.accept(div);
    return getSelf();
  }

  default E script(Consumer<Script> scriptConsumer) {
    final var script = new Script(getSelf());
    scriptConsumer.accept(script);
    return getSelf();
  }

  default E a(Consumer<A> aConsumer) {
    final var a = new A(getSelf());
    aConsumer.accept(a);
    return getSelf();
  }

  default E nav(Consumer<Nav> navConsumer) {
    final var nav = new Nav(getSelf());
    navConsumer.accept(nav);
    return getSelf();
  }
}
