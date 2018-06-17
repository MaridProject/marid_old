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
package org.marid.ui.webide.prefs.repositories;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.marid.applib.dialogs.MaridDialog;
import org.marid.applib.image.AppIcon;
import org.marid.applib.image.WithImages;
import org.marid.applib.model.RepositoryItem;
import org.marid.applib.validators.InputValidators;
import org.marid.spring.annotation.PrototypeScoped;
import org.marid.spring.init.Init;
import org.marid.ui.webide.base.dao.RepositoryDao;
import org.marid.ui.webide.prefs.PrefShell;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toUnmodifiableList;
import static org.eclipse.jface.dialogs.IDialogConstants.CANCEL_ID;
import static org.eclipse.jface.dialogs.IDialogConstants.PROCEED_ID;
import static org.marid.applib.utils.Locales.m;
import static org.marid.applib.utils.Locales.s;

@Component
@PrototypeScoped
public class RepositoryAddDialog extends MaridDialog implements WithImages {

  private String name = "repository";
  private String selector;

  public RepositoryAddDialog(PrefShell shell) {
    super(shell, CANCEL_ID, PROCEED_ID);
  }

  @Override
  public void create() {
    super.create();
    getShell().setText(s("addRepository"));
    getShell().setImage(image(AppIcon.REPOSITORY));
  }

  @Init
  public void name() {
    onInit.add(p -> {
      final var label = new Label(p, SWT.NONE);
      label.setText(s("name") + ": ");
    });
    onInit.add(p -> {
      final var text = new Text(p, SWT.SINGLE | SWT.BORDER);
      text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      text.setText(name);
      text.addListener(SWT.Modify, e -> name = text.getText());
    });
  }

  @Init
  public void selector(RepositoryDao dao, RepositoryStore store) {
    onInit.add(p -> {
      final var label = new Label(p, SWT.NONE);
      label.setText(s("selector") + ": ");
    });
    onInit.add(p -> {
      final var combo = new Combo(p, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
      final var entries = dao.selectors().entrySet().stream()
          .peek(e -> combo.add(e.getValue().getName() + ": " + e.getValue().getDescription()))
          .collect(toUnmodifiableList());
      combo.addListener(SWT.Selection, e -> selector = entries.get(combo.getSelectionIndex()).getKey());
      if (combo.getItemCount() > 0) {
        combo.select(0);
        selector = entries.get(0).getKey();
      }
      onPressed.add(id -> {
        switch (id) {
          case PROCEED_ID: {
            final var validator = InputValidators.inputs(
                InputValidators.fileName(),
                InputValidators.input(v -> v.filter(store::contains).map(e -> m("duplicateItem", e)))
            );
            final var message = validator.isValid(name);
            if (message != null) {
              throw new IllegalStateException(message);
            }
            final var item = new RepositoryItem(name);
            item.setSelector(selector);
            store.add(List.of(item));
            break;
          }
        }
      });
    });
  }
}
