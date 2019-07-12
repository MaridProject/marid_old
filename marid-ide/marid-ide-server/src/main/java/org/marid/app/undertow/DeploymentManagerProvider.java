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

import io.undertow.server.HttpHandler;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;

@Component
public class DeploymentManagerProvider implements AutoCloseable, InitializingBean {

  private final DeploymentManager deploymentManager;

  public DeploymentManagerProvider(DeploymentInfo deploymentInfo) {
    deploymentManager = Servlets.defaultContainer().addDeployment(deploymentInfo);
  }

  public HttpHandler start() throws ServletException {
    return deploymentManager.start();
  }

  @Override
  public void close() throws Exception {
    deploymentManager.stop();
    deploymentManager.undeploy();
  }

  @Override
  public void afterPropertiesSet() {
    deploymentManager.deploy();
  }
}
