/*-
 * #%L
 * marid-ide-server
 * %%
 * Copyright (C) 2012 - 2019 MARID software development group
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

package org.marid.app;

import org.marid.logging.MaridLogManager;
import org.marid.spring.LoggingPostProcessor;
import org.springframework.context.annotation.*;
import org.springframework.context.support.GenericApplicationContext;
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

    final var context = new GenericApplicationContext();
    final var reader = new AnnotatedBeanDefinitionReader(context);

    context.setId("marid");
    context.setDisplayName("Marid Web Application");
    context.setAllowCircularReferences(false);
    context.setAllowBeanDefinitionOverriding(false);
    context.getEnvironment().setDefaultProfiles("release");
    context.getBeanFactory().addBeanPostProcessor(new LoggingPostProcessor());
    context.registerShutdownHook();
    reader.register(Context.class);
    context.getEnvironment().getPropertySources().addFirst(new SimpleCommandLinePropertySource(args));
    context.refresh();
    context.start();
  }
}
