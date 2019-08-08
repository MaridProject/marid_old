package org.marid.app;

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

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static java.util.logging.Level.WARNING;
import static org.marid.logging.Log.log;

@Component
public class ReleaseContext {

  @Bean
  public Closeable pidFile() throws IOException {
    final var pidFile = new File("marid-webapp.pid");
    Files.write(pidFile.toPath(), List.of(Long.toString(ProcessHandle.current().pid())));
    pidFile.deleteOnExit();
    return () -> {
      if (!pidFile.delete()) {
        log(WARNING, "Unable to delete {0}", pidFile);
      }
    };
  }
}
