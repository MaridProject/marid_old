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
