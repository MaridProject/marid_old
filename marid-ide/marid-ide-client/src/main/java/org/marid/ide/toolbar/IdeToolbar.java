package org.marid.ide.toolbar;

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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.marid.ide.images.ImageCache;
import org.marid.spring.init.Init;
import org.springframework.stereotype.Component;

@Component
public class IdeToolbar extends ToolBar {

  public IdeToolbar(Shell mainShell) {
    super(mainShell, SWT.HORIZONTAL | SWT.BORDER);

    setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
  }

  @Init
  public void addCreateButton(ImageCache imageCache) {
    final var button = new ToolItem(this, SWT.PUSH);
    button.setImage(imageCache.image("create.png"));
    button.setToolTipText("Create a new project");

    new ToolItem(this, SWT.SEPARATOR);
  }

  @Init
  public void addOpenButton(ImageCache imageCache) {
    final var button = new ToolItem(this, SWT.PUSH);
    button.setImage(imageCache.image("open.png"));
    button.setToolTipText("Open an existing project");
  }

  @Init
  public void addSaveButton(ImageCache imageCache) {
    final var button = new ToolItem(this, SWT.PUSH);
    button.setImage(imageCache.image("save.png"));
    button.setToolTipText("Save the current project");
  }

  @Override
  protected void checkSubclass() {
  }
}
