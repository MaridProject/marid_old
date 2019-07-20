package org.marid.app.web.rap;

/*-
 * #%L
 * marid-ide-server
 * %%
 * Copyright (C) 2012 - 2019 MARID software development group
 * %%
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * #L%
 */

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
