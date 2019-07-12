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
