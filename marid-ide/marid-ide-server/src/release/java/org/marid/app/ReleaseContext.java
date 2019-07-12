package org.marid.app;

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
