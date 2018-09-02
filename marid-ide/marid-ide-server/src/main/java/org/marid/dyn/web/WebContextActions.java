package org.marid.dyn.web;

/*-
 * #%L
 * marid-ide-server
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

import org.marid.app.web.router.RoutingActions;
import org.marid.html.Html;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.xml.transform.stream.StreamResult;

@Component
public class WebContextActions extends RoutingActions {

  @PostConstruct
  public void initIndex() {
    map.put("index.html", (request, response) -> {
      response.setContentType("text/html");
      response.setCharacterEncoding("UTF-8");
      new Html()
          .head(head -> head
              .title("Marid IDE")
              .meta(meta -> meta.value("google", "notranslate"))
              .meta(meta -> meta.value("viewport", "width=device-width, initial-scale=1.0"))
              .utf8()
              .icon("/public/marid32.png")
              .stylesheet("/webjars/bootstrap/4.1.3/css/bootstrap.css")
              .script("/webjars/jquery/3.3.1-1/jquery.js")
              .script("/webjars/bootstrap/4.1.3/js/bootstrap.bundle.js")
          )
          .body(body -> body
              .nav(nav -> nav
                  .klass("navbar sticky-top navbar-dark bg-primary")
                  .a(a -> a.href("#").content("Sticky Top").klass("navbar-brand"))
              )
          )
          .write(new StreamResult(response.getWriter()));
    });
  }
}
