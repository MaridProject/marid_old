/*-
 * #%L
 * marid-ide-server
 * %%
 * Copyright (C) 2012 - 2019 MARID software development group
 * %%
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * #L%
 */
package org.marid.app.undertow;

import io.undertow.server.handlers.resource.ResourceManager;
import io.undertow.servlet.api.CrawlerSessionManagerConfig;
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

  public MaridDeploymentInfo(ResourceManager resourceManager) {
    setDeploymentName("marid");
    setClassLoader(Thread.currentThread().getContextClassLoader());
    setDefaultEncoding("UTF-8");
    setDisableCachingForSecuredPages(true);
    setInvalidateSessionOnLogout(true);
    setSecurityDisabled(true);
    setContextPath("/");
    setCrawlerSessionManagerConfig(new CrawlerSessionManagerConfig());
    setEagerFilterInit(true);
    setSessionManagerFactory(new InMemorySessionManagerFactory());
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
