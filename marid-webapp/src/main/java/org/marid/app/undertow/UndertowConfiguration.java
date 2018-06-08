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

package org.marid.app.undertow;

import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.CanonicalPathHandler;
import io.undertow.server.handlers.RedirectHandler;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentManager;
import org.marid.app.props.UndertowProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import javax.servlet.ServletException;

@Component
public class UndertowConfiguration {

  @Bean(initMethod = "deploy", destroyMethod = "stop")
  public DeploymentManager deploymentManager(DeploymentProvider deploymentProvider) {
    return Servlets.defaultContainer().addDeployment(deploymentProvider.getDeploymentInfo());
  }

  @Bean
  public HttpHandler servletHandler(DeploymentManager deploymentManager) throws ServletException {
    return deploymentManager.start();
  }

  @Bean
  public HttpHandler rootHandler(HttpHandler servletHandler) {
    return new CanonicalPathHandler(exchange -> {
      switch (exchange.getRelativePath()) {
        case "/":
          new RedirectHandler("/app").handleRequest(exchange);
          break;
        default:
          servletHandler.handleRequest(exchange);
          break;
      }
    });
  }

  @Bean(initMethod = "start", destroyMethod = "stop")
  public Undertow undertow(SSLContext sslContext, UndertowProperties properties, HttpHandler rootHandler) {
    return Undertow.builder()
        .setServerOption(UndertowOptions.ENABLE_HTTP2, true)
        .setServerOption(UndertowOptions.HTTP2_SETTINGS_ENABLE_PUSH, true)
        .addListener(new Undertow.ListenerBuilder()
            .setType(Undertow.ListenerType.HTTPS)
            .setHost(properties.getHost())
            .setPort(properties.getPort())
            .setRootHandler(rootHandler)
            .setSslContext(sslContext)
        )
        .build();
  }
}
