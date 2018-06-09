/*-
 * #%L
 * marid-webapp
 * %%
 * Copyright (C) 2012 - 2018 MARID software development group
 * %%
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * #L%
 */
package org.marid.app.undertow;

import io.undertow.server.session.SslSessionConfig;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.FilterInfo;
import io.undertow.servlet.api.ListenerInfo;
import io.undertow.servlet.api.ServletInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.DispatcherType;

@Component
public class DeploymentProvider {

  private final DeploymentInfo deploymentInfo = new DeploymentInfo()
      .setDeploymentName("marid")
      .setClassLoader(Thread.currentThread().getContextClassLoader())
      .setDefaultEncoding("UTF-8")
      .setDisableCachingForSecuredPages(true)
      .setInvalidateSessionOnLogout(true)
      .setSecurityDisabled(true)
      .setContextPath("/")
      .setSessionConfigWrapper((sessionConfig, deployment) -> new SslSessionConfig(deployment.getSessionManager()))
      .addWelcomePage("/app")
      .setDefaultSessionTimeout(3600);

  @Autowired
  public void setResourseManager(MaridResourceManager resourseManager) {
    deploymentInfo.setResourceManager(resourseManager);
  }

  @Autowired
  public void setServlets(ServletInfo[] servlets) {
    deploymentInfo.addServlets(servlets);
  }

  @Autowired
  public void setFilters(FilterInfo[] filters) {
    deploymentInfo.addFilters(filters);
    deploymentInfo.addFilterUrlMapping("securityFilter", "/app", DispatcherType.REQUEST);
  }

  @Autowired
  public void setListeners(ListenerInfo[] listeners) {
    deploymentInfo.addListeners(listeners);
  }

  public DeploymentInfo getDeploymentInfo() {
    return deploymentInfo;
  }
}
