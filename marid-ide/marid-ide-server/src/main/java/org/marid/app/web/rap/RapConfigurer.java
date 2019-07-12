package org.marid.app.web.rap;

import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.eclipse.rap.rwt.application.ApplicationRunner;
import org.eclipse.rap.rwt.engine.RWTServlet;
import org.marid.app.common.Directories;
import org.marid.app.web.ServletContextConfigurer;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;

@Component
public class RapConfigurer implements ServletContextConfigurer {

  private final RapAppConfig appConfig;
  private final Directories directories;

  private ApplicationRunner runner;

  public RapConfigurer(RapAppConfig appConfig, Directories directories) {
    this.appConfig = appConfig;
    this.directories = directories;
  }

  @Override
  public void start(ServletContext context) {
    context.setAttribute(ApplicationConfiguration.RESOURCE_ROOT_LOCATION, directories.getRwtDir().toAbsolutePath().toString());

    runner = new ApplicationRunner(appConfig, context);
    runner.start();

    final var r = context.addServlet("ideServlet", new RWTServlet());
    r.addMapping("*.ide");
    r.setLoadOnStartup(6);
    r.setAsyncSupported(true);
  }

  @Override
  public void stop(ServletContext context) {
      runner.stop();
  }

  @Override
  public boolean isStopNeeded() {
    return true;
  }
}
