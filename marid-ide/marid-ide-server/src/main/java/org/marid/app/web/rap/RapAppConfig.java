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
    application.addEntryPoint("/index.ide", () -> () -> {
      final var session = RWT.getUISession();
      final var thread = Thread.currentThread();
      final var httpSession = session.getHttpSession();
      httpSession.setMaxInactiveInterval(60);

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
        try {
          display.close();
        } finally {
          try {
            RWT.getRequest().logout();
          } catch (Exception e) {
            httpSession.invalidate();
          } finally {
            thread.interrupt();
          }
        }
      }

      return 0;
    }, Map.of(WebClient.PAGE_TITLE, "Marid IDE"));
  }
}
