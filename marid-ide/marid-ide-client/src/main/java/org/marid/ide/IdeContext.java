package org.marid.ide;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
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

  @Bean
  public ToolBar toolBar(Shell mainShell) {
    final var toolBar = new ToolBar(mainShell, SWT.HORIZONTAL);
    toolBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    return toolBar;
  }

  @Bean
  public Composite composite(Shell mainShell) {
    final var composite = new Composite(mainShell, SWT.NONE);
    composite.setLayout(new GridLayout(1, false));
    composite.setLayoutData(new GridData(GridData.FILL_BOTH));
    return composite;
  }

  @Bean
  public Button button(Composite composite) {
    final var button = new Button(composite, SWT.PUSH);
    button.setText("xxx");
    return button;
  }

  @EventListener
  public void onStart(ContextStartedEvent event) {
    final var shell = event.getApplicationContext().getBean("mainShell", Shell.class);
    shell.layout();
    shell.open();
  }
}
