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
package org.marid.app.undertow;

import io.undertow.server.handlers.resource.ResourceManager;
import io.undertow.server.session.SslSessionConfig;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.ListenerInfo;
import io.undertow.servlet.api.ServletSessionConfig;
import io.undertow.servlet.util.ImmediateInstanceFactory;
import org.marid.app.web.MaridListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionListener;
import java.util.Collections;

import static javax.servlet.SessionTrackingMode.SSL;

@Component
public class MaridDeploymentInfo extends DeploymentInfo {

  public MaridDeploymentInfo(ResourceManager resourceManager) {
    setDeploymentName("marid");
    setClassLoader(Thread.currentThread().getContextClassLoader());
    setDefaultEncoding("UTF-8");
    setDisableCachingForSecuredPages(true);
    setInvalidateSessionOnLogout(true);
    setSecurityDisabled(true);
    setContextPath("/");
    setSessionConfigWrapper((sessionConfig, deployment) -> new SslSessionConfig(deployment.getSessionManager()));
    setDefaultSessionTimeout(3600);
    setCheckOtherSessionManagers(false);
    setServletSessionConfig(new ServletSessionConfig().setSessionTrackingModes(Collections.singleton(SSL)));
    setResourceManager(resourceManager);
  }

  @Autowired
  public void setListeners(MaridListener listener) {
    addListener(new ListenerInfo(ServletContextListener.class, new ImmediateInstanceFactory<>(listener), false));
    addListener(new ListenerInfo(HttpSessionListener.class, new ImmediateInstanceFactory<>(listener), true));
  }
}
