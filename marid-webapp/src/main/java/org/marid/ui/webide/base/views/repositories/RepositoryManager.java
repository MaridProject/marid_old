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
package org.marid.ui.webide.base.views.repositories;

import org.marid.applib.repository.Repository;
import org.marid.ui.webide.base.dao.RepositoryDao;
import org.marid.applib.model.RepositoryItem;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class RepositoryManager {

  private final RepositoryDao dao;
  private final ArrayList<RepositoryItem> repositories = new ArrayList<>();

  public RepositoryManager(RepositoryDao dao) {
    this.dao = dao;
  }

  @PostConstruct
  public void init() {
    repositories.addAll(dao.repositories());
  }

  public void add(RepositoryItem repositoryItem) {
    repositories.add(repositoryItem);
  }

  public void remove(RepositoryItem repositoryItem) {
    repositories.remove(repositoryItem);
  }

  public void save() {
    for (final var repository : repositories) {
      dao.save(repository);
    }
  }

  public boolean isNew(String repositoryName) {
    return repositories.stream().map(RepositoryItem::getName).noneMatch(repositoryName::equals);
  }

  public List<Repository> repositories() {
    final var providers = dao.selectors();
    return repositories.stream()
        .flatMap(e -> Stream.ofNullable(providers.get(e.getSelector())).map(e::repository))
        .filter(Objects::nonNull)
        .collect(Collectors.toUnmodifiableList());
  }
}
