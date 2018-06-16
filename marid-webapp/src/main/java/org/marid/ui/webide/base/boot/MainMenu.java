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
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.marid.applib.controls.DropDownToolItem;
import org.marid.applib.image.AppIcon;
import org.marid.applib.image.WithImages;
import org.marid.applib.utils.Locales;
import org.marid.spring.annotation.SpringComponent;
import org.marid.spring.init.Init;
import org.marid.ui.webide.base.UI;

import static org.eclipse.swt.SWT.*;
import static org.eclipse.swt.layout.GridData.FILL_HORIZONTAL;

@SpringComponent
public class MainMenu extends ToolBar implements WithImages {

  private final DropDownToolItem sessionItem;

  public MainMenu(UI ui) {
    super(ui.shell, BORDER | WRAP | SHADOW_OUT);
    setLayoutData(new GridData(FILL_HORIZONTAL));

    sessionItem = new DropDownToolItem(this);
    sessionItem.setImage(maridIcon(24));
  }

  @Init
  public void closeSessionItem() {
    final MenuItem item = new MenuItem(sessionItem.menu, SWT.PUSH);
    item.setText(Locales.s("closeSession"));
    item.setImage(image(AppIcon.CLOSE));
    item.addListener(SWT.Selection, e -> {
      final var jsExecutor = RWT.getClient().getService(JavaScriptExecutor.class);
      jsExecutor.execute("window.location.replace('/logout')");
    });
  }
}
