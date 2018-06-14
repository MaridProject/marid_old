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
package org.marid.applib.model;

import org.jetbrains.annotations.NotNull;
import org.marid.applib.repository.Repository;
import org.marid.applib.repository.RepositoryProvider;

import java.util.ArrayList;
import java.util.TreeMap;

import static org.marid.function.Suppliers.reduce;

public class RepositoryItem implements Id<String> {

  private final String selector;
  private final String name;
  private final ArrayList<RepositoryProperty> properties = new ArrayList<>();

  public RepositoryItem(String selector, String name) {
    this.selector = selector;
    this.name = name;
  }

  public String getSelector() {
    return selector;
  }

  public ArrayList<RepositoryProperty> getProperties() {
    return properties;
  }

  @NotNull
  @Override
  public String getId() {
    return name;
  }

  public Repository repository(RepositoryProvider provider) {
    return provider.getRepository(
        name,
        reduce(properties.stream(), new TreeMap<>(), (a, e) -> a.put(e.getKey(), e.getValue()))
    );
  }
}
