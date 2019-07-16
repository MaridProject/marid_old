package org.marid.app.web.rap;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.JavaScriptExecutor;
import org.eclipse.swt.internal.widgets.IDisplayAdapter;
import org.eclipse.swt.widgets.Display;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class RapComponents {

  @Bean
  public Runnable exitCommand() {
    return () -> {
      RWT.getClient().getService(JavaScriptExecutor.class).execute("document.location.assign('/logout')");
      final var display = Display.findDisplay(Thread.currentThread());
      final var displayAdapter = display.getAdapter(IDisplayAdapter.class);
      for (final var shell : displayAdapter.getShells()) {
        shell.close();
      }
    };
  }
}
