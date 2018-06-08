/*-
 * #%L
 * marid-webapp
 * %%
 * Copyright (C) 2012 - 2018 MARID software development group
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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

public class MainUI implements EntryPoint {

  public static final String CONTEXT_KEY = "applicationContext";

  private final GenericApplicationContext parent;

  public MainUI(GenericApplicationContext parent) {
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
