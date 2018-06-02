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
package org.marid.ui.webide.base.views.artifacts;

import com.vaadin.icons.VaadinIcons;
import org.marid.applib.components.Toolbar;
import org.marid.applib.dialog.Dialog;
import org.marid.applib.repository.Repository;
import org.marid.applib.spring.init.Init;
import org.marid.applib.spring.init.Inits;
import org.marid.spring.annotation.SpringComponent;
import org.marid.ui.webide.base.views.repositories.RepositoryManager;

import java.util.TreeMap;
import java.util.stream.Collectors;

import static org.marid.applib.utils.Locales.s;

@SpringComponent
public class ArtifactToolbar extends Toolbar implements Inits {

  @Init
  public void initFind(RepositoryManager manager) {
    button(VaadinIcons.SEARCH_PLUS, e -> {
      final var finders = manager.repositories().stream()
          .map(Repository::getArtifactFinder)
          .collect(Collectors.toUnmodifiableList());
      new Dialog<>(s("searchArtifacts"), new TreeMap<String, String>(), 400, 300)
          .addTextField(s("group"), "", (f, b) -> b.bind(m -> m.get("group"), (m, v) -> m.put("group", v)))
          .addTextField(s("artifact"), "", (f, b) -> b.bind(m -> m.get("artifact"), (m, v) -> m.put("artifact", v)))
          .addTextField(s("class"), "", (f, b) -> b.bind(m -> m.get("class"), (m, v) -> m.put("class", v)))
          .addSubmitButton(s("find"), map -> {

          })
          .show();
    }, "searchArtifacts");
  }
}
