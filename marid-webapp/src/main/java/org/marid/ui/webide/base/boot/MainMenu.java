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

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.JavaScriptExecutor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.marid.applib.image.AppIcon;
import org.marid.applib.utils.Locales;
import org.marid.spring.annotation.SpringComponent;
import org.marid.spring.init.Init;
import org.marid.ui.webide.base.UI;
import org.marid.ui.webide.base.common.UserImages;

import static java.awt.Color.CYAN;
import static org.eclipse.swt.SWT.*;
import static org.eclipse.swt.layout.GridData.FILL_HORIZONTAL;
import static org.marid.applib.listeners.SelectListener.selectListener;

@SpringComponent
public class MainMenu extends ToolBar {

  private final ToolItem sessionItem;
  private final Menu sessionMenu;

  public MainMenu(UI ui, UserImages images) {
    super(ui.getShell(), BORDER | WRAP | SHADOW_OUT);
    setLayoutData(new GridData(FILL_HORIZONTAL));

    sessionMenu = new Menu(ui.getShell(), POP_UP);

    sessionItem = new ToolItem(this, DROP_DOWN);
    sessionItem.setImage(images.maridIcon(24));
    sessionItem.setHotImage(images.maridIcon(24, CYAN));
    sessionItem.addSelectionListener(selectListener((d, e) -> {
      final var r = sessionItem.getBounds();
      final var p = toDisplay(r.x, r.y + r.height);

      sessionMenu.setLocation(p);
      sessionMenu.setVisible(true);
    }));
  }

  @Init
  public void closeSessionItem(UserImages images) {
    final MenuItem item = new MenuItem(sessionMenu, SWT.PUSH);
    item.setText(Locales.s("closeSession"));
    item.setImage(images.icon(AppIcon.CLOSE));
    item.addSelectionListener(selectListener((d, e) -> {
      final var jsExecutor = RWT.getClient().getService(JavaScriptExecutor.class);
      jsExecutor.execute("window.location.replace('/logout')");
    }));
  }
}
