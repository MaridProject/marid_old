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
import io.undertow.server.handlers.CanonicalPathHandler;
import io.undertow.server.handlers.RedirectHandler;
import org.marid.app.props.WebProperties;
import org.marid.app.web.PublicHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;

import static io.undertow.UndertowOptions.*;
import static org.xnio.Options.KEEP_ALIVE;

@Component
public class UndertowConfiguration {

  @Bean
  public CanonicalPathHandler rootHandler(PublicHandler publicHandler) {
    return new CanonicalPathHandler(publicHandler);
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
}
