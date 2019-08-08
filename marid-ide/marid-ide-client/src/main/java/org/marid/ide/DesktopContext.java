package org.marid.ide;

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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.marid.ide.images.ImageCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("desktop")
public class DesktopContext {

  @Profile("desktop")
  @Bean(destroyMethod = "")
  public Shell mainShell(Display mainDisplay, ImageCache imageCache) {
    final var shell = new Shell(mainDisplay, SWT.SHELL_TRIM);
    shell.setSize(800, 600);
    shell.setMaximized(true);
    shell.setLayout(new GridLayout(1, false));
    shell.setImages(new Image[] {
        imageCache.icon(16),
        imageCache.icon(24),
        imageCache.icon(32)
    });
    shell.setImage(imageCache.icon(16));
    shell.setText("Marid IDE");
    return shell;
  }
}
