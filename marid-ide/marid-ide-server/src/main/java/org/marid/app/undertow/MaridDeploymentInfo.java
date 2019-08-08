/*-
 * #%L
 * marid-ide-server
 * %%
 * Copyright (C) 2012 - 2019 MARID software development group
 * %%
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 * 
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 * #L%
 */
package org.marid.app.undertow;

import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.ListenerInfo;
import io.undertow.servlet.api.ServletSessionConfig;
import io.undertow.servlet.core.InMemorySessionManagerFactory;
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

  public MaridDeploymentInfo(ClassPathResourceManager metaInfResourceManager) {
    setDeploymentName("marid");
    setClassLoader(Thread.currentThread().getContextClassLoader());
    setDefaultEncoding("UTF-8");
    setDisableCachingForSecuredPages(true);
    setInvalidateSessionOnLogout(true);
    setSecurityDisabled(true);
    setContextPath("/");
    setEagerFilterInit(true);
    setSessionManagerFactory(new InMemorySessionManagerFactory());
    setDefaultSessionTimeout(3600);
    setCheckOtherSessionManagers(false);
    setServletSessionConfig(new ServletSessionConfig().setSessionTrackingModes(Collections.singleton(SSL)));
    setResourceManager(metaInfResourceManager);
  }

  @Autowired
  public void setListeners(MaridListener listener) {
    addListener(new ListenerInfo(ServletContextListener.class, new ImmediateInstanceFactory<>(listener), false));
    addListener(new ListenerInfo(HttpSessionListener.class, new ImmediateInstanceFactory<>(listener), true));
  }
}
