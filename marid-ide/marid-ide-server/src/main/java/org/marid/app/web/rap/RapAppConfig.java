package org.marid.app.web.rap;

import org.eclipse.rap.rwt.application.Application;
import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RapAppConfig implements ApplicationConfiguration {

  @Override
  public void configure(Application application) {
    application.setOperationMode(Application.OperationMode.SWT_COMPATIBILITY);
    application.addEntryPoint("/index.ide", () -> () -> {
      final var display = new Display();
      final var shell = new Shell(display, SWT.NO_TRIM);
      shell.setMaximized(true);
      shell.setLayout(new GridLayout(1, false));

      final var composite = new ScrolledComposite(shell, SWT.H_SCROLL | SWT.V_SCROLL);
      final var button = new Button(composite, SWT.NONE);

      shell.layout();
      shell.open();
      while (!shell.isDisposed()) {
        if (!display.readAndDispatch()) {
          display.sleep();
        }
      }
      display.dispose();
      return 0;
    }, Map.of());
  }
}
