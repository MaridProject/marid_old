/*-
 * #%L
 * marid-ide-server
 * %%
 * Copyright (C) 2012 - 2019 MARID software development group
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
import io.undertow.server.handlers.CanonicalPathHandler;
import io.undertow.server.handlers.RedirectHandler;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.server.handlers.resource.ResourceHandler;
import io.undertow.server.session.*;
import org.marid.app.props.WebProperties;
import org.marid.app.web.PublicHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import java.util.concurrent.TimeUnit;

import static io.undertow.UndertowOptions.*;
import static org.xnio.Options.KEEP_ALIVE;

@Component
public class UndertowConfiguration {

  @Bean
  public SslSessionConfig sessionConfig(SessionManager sessionManager) {
    return new SslSessionConfig(sessionManager);
  }

  @Bean
  public InMemorySessionManager sessionManager() {
    final var sessionManager = new InMemorySessionManager("marid");
    sessionManager.setDefaultSessionTimeout((int) TimeUnit.MINUTES.toSeconds(30L));
    return sessionManager;
  }

  @Bean
  public SessionAttachmentHandler sessionAttachmentHandler(PublicHandler publicHandler, SessionManager sessionManager, SessionConfig sessionConfig) {
    return new SessionAttachmentHandler(publicHandler, sessionManager, sessionConfig);
  }

  @Bean
  public CanonicalPathHandler rootHandler(SessionAttachmentHandler sessionAttachmentHandler) {
    return new CanonicalPathHandler(sessionAttachmentHandler);
  }

  @Bean(initMethod = "start", destroyMethod = "stop")
  public Undertow undertow(SSLContext sslContext, WebProperties properties, CanonicalPathHandler rootHandler) {
    return Undertow.builder()
        .setIoThreads(4)
        .setWorkerThreads(8)
        .setBufferSize(2048)
        .setDirectBuffers(false)
        .setServerOption(ALWAYS_SET_KEEP_ALIVE, true)
        .setServerOption(ALWAYS_SET_DATE, true)
        .setServerOption(ENABLE_HTTP2, true)
        .setServerOption(HTTP2_SETTINGS_ENABLE_PUSH, true)
        .setServerOption(ENABLE_RFC6265_COOKIE_VALIDATION, true)
        .setServerOption(NO_REQUEST_TIMEOUT, 600_000)
        .setSocketOption(KEEP_ALIVE, true)
        .addListener(new Undertow.ListenerBuilder()
            .setType(Undertow.ListenerType.HTTPS)
            .setHost(properties.getHost())
            .setPort(properties.getPort())
            .setSslContext(sslContext)
            .setRootHandler(rootHandler)
        )
        .addListener(new Undertow.ListenerBuilder()
            .setType(Undertow.ListenerType.HTTP)
            .setHost(properties.getHost())
            .setPort(properties.getInsecurePort())
            .setRootHandler(e -> {
              final var path = "https://" + e.getHostName() + ":" + properties.getPort() + e.getRelativePath();
              new RedirectHandler(path).handleRequest(e);
            })
        )
        .build();
  }

  @Bean
  public ClassPathResourceManager metaInfResources() {
    return new ClassPathResourceManager(Thread.currentThread().getContextClassLoader(), "META-INF/resources");
  }

  @Bean
  public ResourceHandler metaInfResourcesHandler(ClassPathResourceManager metaInfResources) {
    return new ResourceHandler(metaInfResources);
  }
}
