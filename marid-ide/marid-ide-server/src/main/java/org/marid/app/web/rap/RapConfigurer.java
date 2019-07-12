package org.marid.app.web.rap;

import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.eclipse.rap.rwt.application.ApplicationRunner;
import org.eclipse.rap.rwt.engine.RWTServlet;
import org.marid.app.web.ServletContextConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class RapConfigurer implements ServletContextConfigurer {

  private static final Logger LOGGER = LoggerFactory.getLogger(RapConfigurer.class);

  private final RapAppConfig appConfig;
  private final Path tmpDir;

  private ApplicationRunner runner;

  public RapConfigurer(RapAppConfig appConfig) throws IOException {
    this.appConfig = appConfig;
    this.tmpDir = Files.createTempDirectory("rwt");
  }

  @Override
  public void start(ServletContext context) {
    context.setAttribute(ApplicationConfiguration.RESOURCE_ROOT_LOCATION, tmpDir.toAbsolutePath().toString());

    runner = new ApplicationRunner(appConfig, context);
    runner.start();

    final var r = context.addServlet("ideServlet", new RWTServlet());
    r.addMapping("*.ide");
    r.setLoadOnStartup(6);
    r.setAsyncSupported(true);
  }

  @Override
  public void stop(ServletContext context) {
    try {
      runner.stop();
      FileSystemUtils.deleteRecursively(tmpDir);
    } catch (IOException e) {
      LOGGER.warn("Unable to stop rap", e);
    }
  }

  @Override
  public boolean isStopNeeded() {
    return true;
  }
}
