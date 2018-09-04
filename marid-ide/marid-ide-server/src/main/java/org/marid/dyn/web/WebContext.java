package org.marid.dyn.web;

import org.marid.spring.annotation.PrototypeScoped;
import org.marid.spring.events.ContextClosedListener;
import org.pac4j.core.profile.CommonProfile;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.context.support.GenericApplicationContext;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.logging.Logger;

import static org.pac4j.core.context.Pac4jConstants.USER_PROFILES;

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
@Configuration
@Scope(proxyMode = ScopedProxyMode.NO)
@ComponentScan
public class WebContext {

  private final String id;
  private final CommonProfile profile;
  private final Logger logger;

  @Autowired(required = false)
  public WebContext(HttpSession session) {
    id = session.getId();
    profile = (CommonProfile) ((Map) session.getAttribute(USER_PROFILES)).values().iterator().next();
    logger = Logger.getLogger(id);
  }

  public Logger getLogger() {
    return logger;
  }

  @Bean
  public CommonProfile profile() {
    return profile;
  }

  @PrototypeScoped
  @Bean
  public Logger logger(InjectionPoint injectionPoint, GenericApplicationContext context) {
    final var name = injectionPoint.getMember().getDeclaringClass().getName();
    final var logger = Logger.getLogger(id + ":" + name);
    logger.setParent(this.logger);
    context.addApplicationListener((ContextClosedListener) event -> logger.setUseParentHandlers(false));
    return logger;
  }
}
