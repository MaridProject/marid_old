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

import com.vaadin.ui.Grid;
import com.vaadin.ui.TextField;
import org.marid.applib.spring.init.Init;
import org.marid.applib.spring.init.Inits;
import org.marid.spring.annotation.SpringComponent;
import org.marid.ui.webide.base.model.Repository;

import static org.marid.applib.utils.Locales.s;

@SpringComponent
public class RepositoryList extends Grid<Repository> implements Inits {

  public RepositoryList() {
    setSizeFull();
    setSelectionMode(SelectionMode.SINGLE);
  }

  @Init
  public void initSelectorColumn() {
    addColumn(Repository::getSelector)
        .setCaption(s("selector"))
        .setExpandRatio(1);
  }

  @Init
  public void initNameColumn() {
    addColumn(Repository::getName)
        .setCaption(s("value"))
        .setExpandRatio(4)
        .setEditorBinding(getEditor().getBinder()
            .forField(new TextField())
            .bind(Repository::getName, Repository::setName)
        )
        .setEditable(true);
  }
}
