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
package org.marid.ui.webide.base.model;

import org.marid.applib.repository.Repository;
import org.marid.applib.repository.RepositoryProvider;

import java.util.ArrayList;
import java.util.TreeMap;

import static org.marid.function.Suppliers.reduce;

public class RepositoryItem {

  private String selector;
  private String name;
  private final ArrayList<RepositoryProperty> properties = new ArrayList<>();

  public RepositoryItem() {
  }

  public RepositoryItem(String selector, String name) {
    this.selector = selector;
    this.name = name;
  }

  public String getSelector() {
    return selector;
  }

  public void setSelector(String selector) {
    this.selector = selector;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ArrayList<RepositoryProperty> getProperties() {
    return properties;
  }

  public Repository repository(RepositoryProvider provider) {
    return provider.getRepository(
        name,
        reduce(properties.stream(), new TreeMap<>(), (a, e) -> a.put(e.getKey(), e.getValue()))
    );
  }
}
