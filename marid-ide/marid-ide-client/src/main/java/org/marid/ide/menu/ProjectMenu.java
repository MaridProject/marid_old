package org.marid.ide.menu;

/*-
 * #%L
 * marid-ide-client
 * %%
 * Copyright (C) 2012 - 2019 MARID software development group
 * %%
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 * 
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 * #L%
 */

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.marid.ide.images.ImageCache;
import org.marid.spring.init.Init;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.stereotype.Component;

@Component
public class ProjectMenu extends Menu {

  public ProjectMenu(Shell mainShell, MenuBar menuBar) {
    super(mainShell, SWT.DROP_DOWN);
    final var menuItem = new MenuItem(menuBar, SWT.CASCADE);
    menuItem.setText("Project");
    menuItem.setMenu(this);
  }

  @Init
  public void initNewProjectItem(ImageCache imageCache) {
    final var item = new MenuItem(this, SWT.PUSH);
    item.setText("Create a new project");
    item.setImage(imageCache.image("create.png"));
    item.addListener(SWT.Selection, e -> {});
    new MenuItem(this, SWT.SEPARATOR);
  }

  @Init
  public void initExitItem(ObjectFactory<Runnable> exitCommand, ImageCache imageCache) {
    final var item = new MenuItem(this, SWT.PUSH);
    item.setText("Exit");
    item.setImage(imageCache.image("shutdown.png"));
    item.addListener(SWT.Selection, e -> exitCommand.getObject().run());
  }

  @Override
  protected void checkSubclass() {
  }
}
