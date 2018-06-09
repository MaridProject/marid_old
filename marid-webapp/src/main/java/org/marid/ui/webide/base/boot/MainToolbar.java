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
package org.marid.ui.webide.base.boot;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ToolBar;
import org.marid.ui.webide.base.UIConfiguration;
import org.springframework.stereotype.Component;

@Component
public class MainToolbar extends ToolBar {

  public MainToolbar(UIConfiguration configuration) {
    super(configuration.getShell(), SWT.WRAP | SWT.SHADOW_OUT | SWT.HORIZONTAL);
  }
}
