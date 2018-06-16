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

import org.marid.applib.dialogs.MaridDialog;
import org.marid.spring.annotation.PrototypeScoped;
import org.marid.ui.webide.base.UI;
import org.springframework.stereotype.Component;

import static org.eclipse.jface.dialogs.IDialogConstants.CANCEL_ID;
import static org.eclipse.jface.dialogs.IDialogConstants.PROCEED_ID;

@Component
@PrototypeScoped
public class RepositoryDialog extends MaridDialog {

  public RepositoryDialog(UI ui) {
    super(ui.shell, CANCEL_ID, PROCEED_ID);
  }
}
