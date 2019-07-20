package org.marid.ide.menu;

/*-
 * #%L
 * marid-ide-client
 * %%
 * Copyright (C) 2012 - 2019 MARID software development group
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
