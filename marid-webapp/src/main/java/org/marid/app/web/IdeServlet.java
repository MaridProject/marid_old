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
package org.marid.app.web;

import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.server.InvalidRouteConfigurationException;
import com.vaadin.flow.server.ServiceException;
import com.vaadin.flow.server.VaadinServlet;
import com.vaadin.flow.server.VaadinServletService;
import org.marid.ui.ide.MainComponent;
import org.marid.ui.ide.MainUI;
import org.springframework.stereotype.Component;

import java.util.Properties;
import java.util.Set;

import static com.vaadin.flow.server.VaadinSession.UI_PARAMETER;

@Component
public class IdeServlet extends VaadinServlet {

  @Override
  protected DeploymentConfiguration createDeploymentConfiguration() {
    final var properties = new Properties();

    properties.setProperty(UI_PARAMETER, MainUI.class.getName());

    return createDeploymentConfiguration(properties);
  }

  @Override
  protected VaadinServletService createServletService(DeploymentConfiguration configuration) throws ServiceException {
    final var service = super.createServletService(configuration);
    try {
      service.getRouter().getRegistry().setNavigationTargets(Set.of(MainComponent.class));
    } catch (InvalidRouteConfigurationException x) {
      throw new ServiceException(x);
    }
    return service;
  }
}
