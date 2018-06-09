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

import org.eclipse.rap.rwt.application.EntryPoint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.marid.applib.spring.ContextUtils;
import org.marid.ui.webide.base.UIConfiguration;
import org.springframework.context.support.GenericApplicationContext;

public class MainEntryPoint implements EntryPoint {

  public static final String CONTEXT_KEY = "applicationContext";

  private final GenericApplicationContext parent;

  public MainEntryPoint(GenericApplicationContext parent) {
    this.parent = parent;
  }

  @Override
  public int createUI() {
    final var display = new Display();
    final var shell = new Shell(display, SWT.NO_TRIM);

    shell.setMaximized(true);

    final var child = ContextUtils.context(parent, c -> {
      c.setId("mainUI");
      c.setDisplayName("mainUI");
      c.registerBean(UIConfiguration.class, () -> new UIConfiguration(shell));
      display.setData(CONTEXT_KEY, c);
    });
    child.refresh();
    child.start();

    try (child) {
      shell.layout();
      shell.open();

      while (!shell.isDisposed()) {
        if (!display.readAndDispatch()) {
          display.sleep();
        }
      }
    } finally {
      display.dispose();
    }

    return 0;
  }
}
