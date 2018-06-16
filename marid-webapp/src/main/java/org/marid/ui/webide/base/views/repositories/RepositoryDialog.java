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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.marid.spring.annotation.PrototypeScoped;
import org.marid.ui.webide.base.UI;
import org.springframework.stereotype.Component;

import static org.eclipse.jface.dialogs.IDialogConstants.CANCEL_ID;
import static org.eclipse.jface.dialogs.IDialogConstants.PROCEED_ID;
import static org.eclipse.swt.SWT.NONE;
import static org.marid.applib.utils.Locales.s;

@Component
@PrototypeScoped
public class RepositoryDialog extends Dialog {

  public RepositoryDialog(UI ui) {
    super(ui.shell);
    setBlockOnOpen(false);
  }

  @Override
  protected Composite createDialogArea(Composite parent) {
    final var panel = new Composite(parent, NONE);
    panel.setLayoutData(new GridData(GridData.FILL_BOTH));

    return panel;
  }

  @Override
  protected void createButtonsForButtonBar(Composite parent) {
    createButton(parent, CANCEL_ID, s("cancel"), false);
    createButton(parent, PROCEED_ID, s("apply"), true);
  }
}
