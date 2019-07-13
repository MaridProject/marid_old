package org.marid.ide;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
public class IdeContext {

  @Bean
  public Composite composite(ObjectFactory<Shell> mainShell) {
    final var composite = new Composite(mainShell.getObject(), SWT.NONE);
    composite.setLayout(new GridLayout(1, false));
    return composite;
  }

  @Bean
  public Button button(Composite composite) {
    final var button = new Button(composite, SWT.PUSH);
    button.setText("xxx");
    return button;
  }
}
