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

import com.vaadin.data.Binder;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import org.marid.applib.components.PropertyEditor;
import org.marid.applib.l10n.Msgs;
import org.marid.applib.l10n.Strs;
import org.marid.applib.spring.init.Init;
import org.marid.applib.spring.init.Inits;
import org.marid.applib.validators.StringValidators;
import org.marid.spring.annotation.PrototypeScoped;
import org.marid.spring.annotation.SpringComponent;

import java.util.Properties;

import static org.marid.applib.utils.Locales.s;

@SpringComponent
@PrototypeScoped
public class AddRepositoryDialog extends Window implements Inits {

  private final FormLayout layout = new FormLayout();
  private final Binder<AddRepositoryDialog> binder = new Binder<>();

  private String name;
  private Properties properties;

  public AddRepositoryDialog(Strs strs) {
    super(strs.s("addRepository"));
    setContent(layout);
    setModal(true);
    layout.setSpacing(true);
    layout.setMargin(true);
  }

  @Init
  public void initName(Strs strs, Msgs msgs) {
    final var field = new TextField(strs.s("name"));
    layout.addComponent(field);
    binder
        .forField(field)
        .asRequired(c -> msgs.m("valueIsRequired"))
        .withValidator(StringValidators.fileNameValidator())
        .bind(d -> d.name, (d, v) -> d.name = v);
  }

  @Init
  public void initProperties() {
    final var grid = new PropertyEditor();
    grid.setCaption(s("properties"));
    layout.addComponent(grid);
    binder
        .forField(grid)
        .bind(d -> d.properties, (d, v) -> d.properties = v);
  }

  @Init
  public void initButton(Strs strs) {
    final var button = new Button(strs.s("add"));
    button.addClickListener(event -> {
      if (binder.writeBeanIfValid(this)) {

        close();
      }
    });
    layout.addComponent(button);
  }
}
