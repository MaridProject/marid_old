package org.marid.ide;

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
