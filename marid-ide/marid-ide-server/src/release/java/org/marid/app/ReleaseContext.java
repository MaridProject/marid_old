package org.marid.app;

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
