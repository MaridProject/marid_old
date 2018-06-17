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
package org.marid.ui.webide.prefs.artifacts;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.marid.spring.annotation.PrototypeScoped;
import org.marid.ui.webide.prefs.PrefShell;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@PrototypeScoped
public class ArtifactFindDialog extends Shell {

  private final TabFolder tabs;

  public ArtifactFindDialog(PrefShell shell) {
    super(shell, SWT.TITLE | SWT.CLOSE | SWT.APPLICATION_MODAL);
    setMaximized(true);
    setLayout(new GridLayout(1, false));
    tabs = new TabFolder(this, SWT.BORDER);
    tabs.setLayoutData(new GridData(GridData.FILL_BOTH));
  }

  @PostConstruct
  @Override
  public void open() {
    super.open();
  }
}
