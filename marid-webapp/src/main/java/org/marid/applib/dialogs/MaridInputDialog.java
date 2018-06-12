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
package org.marid.applib.dialogs;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Control;

import java.util.Optional;
import java.util.function.Consumer;

public class MaridInputDialog extends InputDialog {

  private Consumer<Optional<String>> onClose = o -> {};

  public MaridInputDialog(Control control, String title, String message, String value, IInputValidator validator) {
    super(control.getShell(), title, message, value, validator);
    setBlockOnOpen(false);
  }

  public MaridInputDialog setOnClose(Consumer<Optional<String>> onClose) {
    this.onClose = onClose;
    return this;
  }

  @Override
  public boolean close() {
    if (super.close()) {
      switch (getReturnCode()) {
        case OK:
          onClose.accept(Optional.ofNullable(getValue()));
          break;
        default:
           onClose.accept(Optional.empty());
          break;
      }
      return true;
    } else {
      return false;
    }
  }
}
