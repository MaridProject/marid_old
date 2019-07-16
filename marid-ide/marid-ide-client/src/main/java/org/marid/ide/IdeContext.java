package org.marid.ide;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
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
  public Canvas composite(Shell mainShell) {
    final var canvas = new Canvas(mainShell, SWT.NONE);
    canvas.setLayoutData(new GridData(GridData.FILL_BOTH));
    final var display = mainShell.getDisplay();
    canvas.setBackground(display.getSystemColor(SWT.COLOR_LIST_BACKGROUND));
    return canvas;
  }

  @EventListener
  public void onStart(ContextStartedEvent event) {
    final var shell = event.getApplicationContext().getBean("mainShell", Shell.class);
    shell.layout();
    shell.open();
  }
}
