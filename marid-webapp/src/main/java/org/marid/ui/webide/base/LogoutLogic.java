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
package org.marid.ui.webide.base;

import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.server.VaadinServletResponse;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.engine.DefaultLogoutLogic;
import org.springframework.stereotype.Component;

@Component
public class LogoutLogic {

  private final Config config;
  private final DefaultLogoutLogic<Void, J2EContext> logic = new DefaultLogoutLogic<>();

  public LogoutLogic(Config config) {
    this.config = config;
  }

  public Runnable logoutTask() {
    final var request = (VaadinServletRequest) VaadinRequest.getCurrent();
    final var response = (VaadinServletResponse) VaadinResponse.getCurrent();
    final var context = new J2EContext(request.getHttpServletRequest(), response.getHttpServletResponse());
    return () -> logic.perform(context, config, (code, ctx) -> null, "/", null, true, false, false);
  }
}
