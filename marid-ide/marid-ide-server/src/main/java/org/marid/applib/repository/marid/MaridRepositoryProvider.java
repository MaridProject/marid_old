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
package org.marid.applib.repository.marid;

import org.marid.applib.repository.RepositoryProvider;

import java.util.Map;
import java.util.Properties;

public class MaridRepositoryProvider implements RepositoryProvider {

  @Override
  public MaridRepository getRepository(String name, Map<String, String> properties) {
    return new MaridRepository(name);
  }

  @Override
  public Properties getProperties() {
    return new Properties();
  }

  @Override
  public String getName() {
    return "Marid";
  }

  @Override
  public String getDescription() {
    return "Marid Proprietary Repository";
  }
}
