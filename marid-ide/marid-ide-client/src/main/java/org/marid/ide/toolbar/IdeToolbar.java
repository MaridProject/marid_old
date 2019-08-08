package org.marid.ide.toolbar;

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
