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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.intellij.lang.annotations.MagicConstant;

import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

import static org.eclipse.jface.dialogs.IDialogConstants.*;
import static org.marid.applib.utils.Locales.s;

public abstract class MaridDialog extends Dialog {

  private final int[] buttons;
  protected final LinkedList<IntConsumer> onPressed = new LinkedList<>();
  protected final LinkedList<Consumer<Composite>> onInit = new LinkedList<>();

  public MaridDialog(Shell shell, @MagicConstant(valuesFromClass = IDialogConstants.class) int... buttons) {
    super(shell);
    this.buttons = buttons;
    setBlockOnOpen(false);
  }

  @Override
  protected final void buttonPressed(int buttonId) {
    try {
      onPressed.forEach(c -> c.accept(buttonId));
      close();
    } catch (Exception x) {
      final var msg = new MessageBox(getShell(), SWT.OK | SWT.ICON_ERROR | SWT.APPLICATION_MODAL);
      msg.setMarkupEnabled(true);
      msg.setMessage(x.getLocalizedMessage());
      msg.open(returnCode -> {});
    } finally {
      setReturnCode(buttonId);
    }
  }

  @Override
  protected final void okPressed() {
  }

  @Override
  protected final void cancelPressed() {
  }

  @Override
  protected Composite getDialogArea() {
    return (Composite) super.getDialogArea();
  }

  @Override
  protected Composite createDialogArea(Composite parent) {
    final var composite = new Composite(parent, SWT.NONE);
    composite.setLayout(new GridLayout(2, false));
    composite.setLayoutData(new GridData(GridData.FILL_BOTH));
    onInit.forEach(c -> c.accept(composite));
    return composite;
  }

  @Override
  protected void createButtonsForButtonBar(Composite parent) {
    for (final int buttonId : buttons) {
      switch (buttonId) {
        case OK_ID: createButton(parent, buttonId, s("ok"), true); break;
        case CANCEL_ID: createButton(parent, buttonId, s("cancel"), true); break;
        case PROCEED_ID: createButton(parent, buttonId, s("proceed"), false); break;
        case ABORT_ID: createButton(parent, buttonId, s("abort"), false); break;
        case NEXT_ID: createButton(parent, buttonId, s("next"), false); break;
        case BACK_ID: createButton(parent, buttonId, s("back"), false); break;
        case SELECT_ALL_ID: createButton(parent, buttonId, s("selectAll"), false); break;
        case DESELECT_ALL_ID: createButton(parent, buttonId, s("deselectAll"), false); break;
        case YES_ID: createButton(parent, buttonId, s("yes"), false); break;
        case NO_ID: createButton(parent, buttonId, s("no"), true); break;
      }
    }
  }
}
