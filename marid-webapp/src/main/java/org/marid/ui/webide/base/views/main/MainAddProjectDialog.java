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
package org.marid.ui.webide.base.views.main;

import com.vaadin.data.Binder;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import org.marid.applib.annotation.PrototypeScoped;
import org.marid.applib.annotation.SpringComponent;
import org.marid.applib.l10n.Msgs;
import org.marid.applib.l10n.Strs;
import org.marid.applib.spring.init.Init;
import org.marid.applib.spring.init.Inits;
import org.marid.ui.webide.base.model.ProjectInfo;

@SpringComponent
@PrototypeScoped
public class MainAddProjectDialog extends Window implements Inits {

  private final FormLayout layout = new FormLayout();
  private final ProjectInfo info = new ProjectInfo();
  private final Binder<ProjectInfo> binder = new Binder<>();

  public MainAddProjectDialog(Strs strs) {
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
        .bind(ProjectInfo::getName, ProjectInfo::setName);
    nameField.addValueChangeListener(event -> nameBinder.validate());
  }

  @Init
  public void initButton(Strs strs, MainViewManager manager) {
    final var button = new Button(strs.s("add"));
    button.addClickListener(event -> {
      if (binder.writeBeanIfValid(info)) {
        manager.add(info);
        close();
      }
    });
    layout.addComponent(button);
  }
}
