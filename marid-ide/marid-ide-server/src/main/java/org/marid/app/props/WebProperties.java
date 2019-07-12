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

package org.marid.app.props;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class WebProperties {

  @Value("${web.host:localhost}")
  private String host;

  @Value("${web.port:8443}")
  private int port;

  @Value("${web.insecurePort:8080}")
  private int insecurePort;

  @Value("${web.session-timeout:1800}")
  private int sessionTimeout;

  public int getSessionTimeout() {
    return sessionTimeout;
  }

  public void setSessionTimeout(int sessionTimeout) {
    this.sessionTimeout = sessionTimeout;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public int getInsecurePort() {
    return insecurePort;
  }

  public void setInsecurePort(int insecurePort) {
    this.insecurePort = insecurePort;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  @Override
  public String toString() {
    return String.format("%s(%s:%s,%s)", getClass().getSimpleName(), host, port, sessionTimeout);
  }
}
