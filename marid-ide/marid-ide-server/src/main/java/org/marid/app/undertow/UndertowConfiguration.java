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

import io.undertow.Undertow;
import io.undertow.security.handlers.SecurityInitialHandler;
import io.undertow.server.handlers.RedirectHandler;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.server.handlers.resource.PathResourceManager;
import org.marid.app.common.Directories;
import org.marid.app.props.WebProperties;
import org.marid.app.web.PublicHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.xnio.OptionMap;
import org.xnio.Options;

import javax.net.ssl.SSLContext;

import static io.undertow.UndertowOptions.*;
import static io.undertow.security.api.AuthenticationMode.CONSTRAINT_DRIVEN;

@Component
public class UndertowConfiguration {

  @Bean(initMethod = "start", destroyMethod = "stop")
  public Undertow undertow(SSLContext sslContext, WebProperties properties, PublicHandler publicHandler) {
    return Undertow.builder()
        .setDirectBuffers(false)

        .setServerOption(ALWAYS_SET_KEEP_ALIVE, true)
        .setServerOption(ENABLE_HTTP2, true)
        .setServerOption(HTTP2_SETTINGS_ENABLE_PUSH, true)
        .setServerOption(NO_REQUEST_TIMEOUT, 60 * 1000)

        .addListener(new Undertow.ListenerBuilder()
            .setType(Undertow.ListenerType.HTTPS)
            .setHost(properties.getHost())
            .setPort(properties.getPort())
            .setSslContext(sslContext)
            .setRootHandler(new SecurityInitialHandler(CONSTRAINT_DRIVEN, null, "oauth2", publicHandler))
            .setOverrideSocketOptions(OptionMap.builder()
                .set(Options.KEEP_ALIVE, true)
                .set(Options.SSL_ENABLED, true)
                .set(Options.SSL_NON_BLOCKING_KEY_MANAGER, true)
                .set(Options.SSL_NON_BLOCKING_TRUST_MANAGER, true)
                .getMap())
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
  public ClassPathResourceManager metaInfResourceManager() {
    return new ClassPathResourceManager(Thread.currentThread().getContextClassLoader(), "META-INF/resources/");
  }

  @Bean
  public ClassPathResourceManager publicResourceManager() {
    return new ClassPathResourceManager(Thread.currentThread().getContextClassLoader(), "public/");
  }

  @Bean
  public PathResourceManager rwtResourceManager(Directories directories) {
    return new PathResourceManager(directories.getRwtDir(), 1024, true, false, false);
  }
}
