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

package org.marid.app.config;

import org.marid.app.props.SslProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.security.KeyStore;

@Component
public class SslConfiguration {

  @Bean
  public KeyStore keyStore(SslProperties properties) throws Exception {
    final var keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
    try (final var inputStream = getClass().getResourceAsStream("/marid.jks")) {
      keyStore.load(inputStream, properties.getPassword().toCharArray());
    }
    return keyStore;
  }

  @Bean
  public KeyManagerFactory keyManagerFactory(KeyStore keyStore, SslProperties properties) throws Exception {
    final var kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
    kmf.init(keyStore, properties.getPassword().toCharArray());
    return kmf;
  }

  @Bean
  public TrustManagerFactory trustManagerFactory(KeyStore keyStore) throws Exception {
    final var tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
    tmf.init(keyStore);
    return tmf;
  }

  @Bean
  public SSLContext sslContext(KeyManagerFactory kmf, TrustManagerFactory tmf) throws Exception {
    final SSLContext context = SSLContext.getInstance("TLS");
    context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
    return context;
  }
}
