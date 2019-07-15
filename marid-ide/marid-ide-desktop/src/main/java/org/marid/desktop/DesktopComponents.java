package org.marid.desktop;

import org.eclipse.swt.widgets.Shell;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class DesktopComponents {

  @Bean
  public Runnable exitCommand(Shell mainShell) {
    return mainShell::close;
  }
}
