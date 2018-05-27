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
package org.marid.ui.webide.base.views.projects;

import com.vaadin.data.Binder;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import org.marid.applib.l10n.Msgs;
import org.marid.applib.l10n.Strs;
import org.marid.applib.spring.init.Init;
import org.marid.applib.spring.init.Inits;
import org.marid.spring.annotation.PrototypeScoped;
import org.marid.spring.annotation.SpringComponent;

import java.util.concurrent.atomic.AtomicReference;

@SpringComponent
@PrototypeScoped
public class AddProjectDialog extends Window implements Inits {

  private final FormLayout layout = new FormLayout();
  private final AtomicReference<String> nameRef = new AtomicReference<>();
  private final Binder<AtomicReference<String>> binder = new Binder<>();

  public AddProjectDialog(Strs strs) {
    super(strs.s("addProject"));
    setModal(true);
    setContent(layout);
    layout.setSpacing(true);
    layout.setMargin(true);
  }

  @Init
  public void initName(Strs strs, Msgs msgs) {
    final var nameField = new TextField(strs.s("name"));
    layout.addComponent(nameField);

    final var nameBinder = binder.forField(nameField)
        .asRequired(msgs.m("nameNonEmpty"))
        .withValidator(new StringLengthValidator(msgs.m("projectNameValidationLength"), 2, 32))
        .bind(AtomicReference::get, AtomicReference::set);
    nameField.addValueChangeListener(event -> nameBinder.validate());
  }

  @Init
  public void initButton(Strs strs, ProjectManager manager) {
    final var button = new Button(strs.s("add"));
    button.addClickListener(event -> {
      if (binder.writeBeanIfValid(nameRef)) {
        manager.add(nameRef.get());
        close();
      }
    });
    layout.addComponent(button);
  }
}
