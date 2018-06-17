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

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Shell;
import org.intellij.lang.annotations.MagicConstant;

public class ShellDialog extends Shell {

  public ShellDialog(Shell parent,
                     @MagicConstant(flagsFromClass = SWT.class) int style,
                     @MagicConstant(valuesFromClass = IDialogConstants.class) int... buttons) {
    super(parent, style);
    setLayout(new GridLayout(1, false));
  }
}
