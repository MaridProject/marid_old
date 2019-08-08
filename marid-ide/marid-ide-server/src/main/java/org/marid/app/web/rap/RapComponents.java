package org.marid.app.web.rap;

/*-
 * #%L
 * marid-ide-server
 * %%
 * Copyright (C) 2012 - 2019 MARID software development group
 * %%
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 * 
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
