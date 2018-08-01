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
package org.marid.app.web;

import org.pac4j.core.config.Config;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.engine.DefaultCallbackLogic;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class CallbackServlet extends HttpServlet {

  private final Config config;
  private final DefaultCallbackLogic<Void, J2EContext> callbackLogic;

  public CallbackServlet(Config config) {
    this.config = config;
    this.callbackLogic = new DefaultCallbackLogic<>();
  }

  @Override
  protected void doGet(HttpServletRequest q, HttpServletResponse r) {
    callbackLogic.perform(new J2EContext(q, r), config, (code, ctx) -> null, "/ide/index.html", true, false, null, null);
  }
}
