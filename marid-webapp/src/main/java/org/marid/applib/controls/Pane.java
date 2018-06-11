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
package org.marid.applib.controls;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;

import static org.eclipse.swt.SWT.*;

public class Pane extends Composite {

  public final ToolBar toolbar;

  public Pane(Composite parent) {
    this(parent, BORDER | WRAP | SHADOW_OUT);
  }

  public Pane(Composite parent, int toolbarStyle) {
    this(parent, NONE, toolbarStyle);
  }

  public Pane(Composite parent, int style, int toolbarStyle) {
    super(parent, style);
    this.toolbar = new ToolBar(this, toolbarStyle);
  }
}
