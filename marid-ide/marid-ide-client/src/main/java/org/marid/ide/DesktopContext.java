package org.marid.ide;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("desktop")
public class DesktopContext {

  @Profile("desktop")
  @Bean(destroyMethod = "")
  public Shell mainShell(Display mainDisplay) {
    final var shell = new Shell(mainDisplay, SWT.SHELL_TRIM);
    shell.setMaximized(true);
    shell.setLayout(new GridLayout(1, false));
    return shell;
  }
}