package org.marid.app.web.rap;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.application.Application;
import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.eclipse.rap.rwt.client.WebClient;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.marid.ide.IdeContext;
import org.marid.spring.LoggingPostProcessor;
import org.marid.spring.init.InitBeanPostProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

import static java.util.Map.entry;

@Component
public class RapAppConfig implements ApplicationConfiguration {

  private static final Logger LOGGER = LoggerFactory.getLogger(RapAppConfig.class);

  private final GenericApplicationContext parent;

  public RapAppConfig(GenericApplicationContext parent) {
    this.parent = parent;
  }

  @Override
  public void configure(Application application) {
    application.setOperationMode(Application.OperationMode.SWT_COMPATIBILITY);
    application.setExceptionHandler(throwable -> LOGGER.error("Application error", throwable));

    setupResources(application);

    application.addEntryPoint("/index.ide", () -> {
      LOGGER.info("Adding /index.ide");
      return () -> {
        final var session = RWT.getUISession();
        final var httpSession = session.getHttpSession();
        httpSession.setMaxInactiveInterval(600);

        final var context = new AnnotationConfigApplicationContext();

        context.setId("ide");
        context.setDisplayName("Marid IDE");
        context.getDefaultListableBeanFactory().setParentBeanFactory(parent.getBeanFactory());
        context.setAllowCircularReferences(false);
        context.setAllowBeanDefinitionOverriding(false);
        context.getEnvironment().setDefaultProfiles("release", "web");
        context.getBeanFactory().addBeanPostProcessor(new LoggingPostProcessor());
        context.getBeanFactory().addBeanPostProcessor(new InitBeanPostProcessor(context));
        context.register(IdeContext.class);

        parent.addApplicationListener((ContextClosedEvent e) -> context.close());

        context.refresh();
        context.start();

        final var display = context.getBean("mainDisplay", Display.class);
        final var shell = context.getBean("mainShell", Shell.class);

        shell.layout();
        shell.open();

        try (context) {
          while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
              display.sleep();
            }
          }
        } finally {
          display.close();
        }

        return 0;
      };
    }, Map.ofEntries(
        entry(WebClient.PAGE_TITLE, "Marid IDE"),
        entry(WebClient.FAVICON, "/marid.png")
    ));
  }

  private void setupResources(Application application) {
    final var classLoader = Thread.currentThread().getContextClassLoader();

    application.addResource("/marid.png", resourceName -> classLoader.getResourceAsStream("public/marid32.png"));
  }
}
