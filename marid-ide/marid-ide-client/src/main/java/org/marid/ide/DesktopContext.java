package org.marid.ide;

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
