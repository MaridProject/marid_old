package org.marid.ide;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;

@Configuration
@ComponentScan
public class IdeContext {

  @Bean(destroyMethod = "")
  public Display mainDisplay() {
    return new Display();
  }

  @EventListener
  public void onStart(ContextStartedEvent event) {
    final var shell = event.getApplicationContext().getBean("mainShell", Shell.class);
    shell.layout();
    shell.open();
  }
}
