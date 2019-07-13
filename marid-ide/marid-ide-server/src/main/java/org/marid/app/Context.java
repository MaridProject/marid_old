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

package org.marid.app;

import org.marid.logging.MaridLogManager;
import org.marid.spring.LoggingPostProcessor;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

import java.util.logging.LogManager;

@EnableScheduling
@ComponentScan
@PropertySource(value = {"application.properties"})
@Configuration
@Scope(proxyMode = ScopedProxyMode.NO)
public class Context {

  @Bean
  public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
    final var configurer = new PropertySourcesPlaceholderConfigurer();
    configurer.setFileEncoding("UTF-8");
    configurer.setNullValue("@null");
    configurer.setIgnoreResourceNotFound(false);
    configurer.setIgnoreUnresolvablePlaceholders(false);
    return configurer;
  }

  @Bean
  public static ConcurrentTaskScheduler taskScheduler() {
    return new ConcurrentTaskScheduler();
  }

  public static void main(String... args) throws Exception {
    System.setProperty("java.util.logging.manager", MaridLogManager.class.getName());

    final var logManager = LogManager.getLogManager();
    try (final var inputStream = Context.class.getResourceAsStream("/app/logging.properties")) {
      logManager.readConfiguration(inputStream);
    }

    final var context = new AnnotationConfigApplicationContext();

    context.setId("marid");
    context.setDisplayName("Marid Web Application");
    context.setAllowCircularReferences(false);
    context.setAllowBeanDefinitionOverriding(false);
    context.getEnvironment().setDefaultProfiles("release");
    context.getBeanFactory().addBeanPostProcessor(new LoggingPostProcessor());
    context.registerShutdownHook();
    context.register(Context.class);
    context.getEnvironment().getPropertySources().addFirst(new SimpleCommandLinePropertySource(args));
    context.refresh();
    context.start();
  }
}
