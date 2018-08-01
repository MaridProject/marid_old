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
