package org.marid.desktop;

import org.marid.logging.MaridConsoleLogHandler;
import org.marid.logging.MaridLogFormatter;
import org.marid.spring.LoggingPostProcessor;
import org.marid.spring.init.InitBeanPostProcessor;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Configuration
@ComponentScan
public class MaridDesktop {

  public static void main(String... args) throws Exception {
    initLogging();

    try (final var classLoader = getClassLoader(Thread.currentThread().getContextClassLoader())) {
      Thread.currentThread().setContextClassLoader(classLoader);

      final var context = new AnnotationConfigApplicationContext();

      context.setId("ide");
      context.setClassLoader(classLoader);
      context.setResourceLoader(new PathMatchingResourcePatternResolver(classLoader));
      context.setDisplayName("Marid IDE");
      context.setAllowCircularReferences(false);
      context.setAllowBeanDefinitionOverriding(false);
      context.getEnvironment().setDefaultProfiles("release", "desktop");
      context.getBeanFactory().addBeanPostProcessor(new LoggingPostProcessor());
      context.getBeanFactory().addBeanPostProcessor(new InitBeanPostProcessor(context));
      context.register(MaridDesktop.class);
      context.scan("org.marid.ide");

      context.refresh();
      context.start();

      final var eventLoop = context.getBean("ideContextFinisher", Runnable.class);
      eventLoop.run();
    }
  }

  private static void initLogging() throws Exception {
    LogManager.getLogManager().reset();

    try (final var is = Thread.currentThread().getContextClassLoader().getResourceAsStream("logging.properties")) {
      if (is != null) {
        LogManager.getLogManager().readConfiguration(is);
      } else {
        final var logger = Logger.getLogger("");
        final var handler = new MaridConsoleLogHandler();
        handler.setFormatter(new MaridLogFormatter());
        logger.addHandler(handler);
      }
    }
  }

  private static URLClassLoader getClassLoader(ClassLoader parent) throws IOException {
    final var swtPrefix = "org.eclipse.swt.";
    try (final var is = Objects.requireNonNull(parent.getResourceAsStream(""))) {
      final var buffer = new ByteArrayOutputStream();
      is.transferTo(buffer);
      final var urls = buffer.toString(StandardCharsets.UTF_8).lines()
          .filter(l -> l.endsWith(".jar"))
          .filter(l -> !l.startsWith(swtPrefix))
          .map(parent::getResource)
          .filter(Objects::nonNull)
          .collect(Collectors.toList());

      final var os = System.getProperty("os.name").toLowerCase();
      final var arch = System.getProperty("os.arch");

      final String s1;
      if (os.contains("linux")) {
        s1 = "gtk.linux";
      } else if (os.contains("windows")) {
        s1 = "win32.win32";
      } else if (os.contains("mac")) {
        s1 = "cocoa.macosx";
      } else {
        throw new IllegalArgumentException("Unknown OS: " + os);
      }

      final String s2;
      switch (arch) {
        case "amd64":
          s2 = "x86_64";
          break;
        case "x86":
          s2 = "x86";
          break;
        default:
          throw new IllegalArgumentException("Unknown architecture: " + arch);
      }

      final var osSpecificPath = swtPrefix + s1 + "." + s2 + ".jar";
      final var osSpecificUrl = parent.getResource(osSpecificPath);
      if (osSpecificUrl == null) {
        throw new IllegalStateException("Unable to locate " + osSpecificPath);
      }

      urls.add(osSpecificUrl);

      return new URLClassLoader(urls.toArray(URL[]::new), parent);
    }
  }
}
