/*-
 * #%L
 * marid-webapp
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
package org.marid.ui.webide.base.views.repositories;

import com.vaadin.data.provider.ListDataProvider;
import org.marid.ui.webide.base.dao.RepositoriesDao;
import org.marid.ui.webide.base.model.Repository;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;

@Component
public class RepositoryManager {

  private final RepositoriesDao dao;
  private final ArrayList<Repository> repositories = new ArrayList<>();
  private final ListDataProvider<Repository> dataProvider = new ListDataProvider<>(repositories);

  public RepositoryManager(RepositoriesDao dao) {
    this.dao = dao;
  }

  @PostConstruct
  public void init() {
    repositories.addAll(dao.repositories());
  }

  public void add(Repository repository) {
    repositories.add(repository);
    dataProvider.refreshAll();
  }

  public void remove(Repository repository) {
    repositories.remove(repository);
    dataProvider.refreshAll();
  }

  public void save() {
    for (final var repository : repositories) {
      dao.save(repository);
    }
  }

  public ListDataProvider<Repository> getDataProvider() {
    return dataProvider;
  }

  public boolean isNew(String repositoryName) {
    return repositories.stream().map(Repository::getName).noneMatch(repositoryName::equals);
  }
}
