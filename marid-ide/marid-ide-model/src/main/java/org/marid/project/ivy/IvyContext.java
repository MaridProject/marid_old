package org.marid.project.ivy;

/*-
 * #%L
 * marid-ide-model
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

import org.apache.ivy.Ivy;
import org.apache.ivy.core.event.EventManager;
import org.apache.ivy.core.resolve.ResolveOptions;
import org.apache.ivy.core.retrieve.RetrieveOptions;
import org.apache.ivy.plugins.resolver.BasicResolver;
import org.apache.ivy.plugins.resolver.ChainResolver;
import org.apache.ivy.plugins.resolver.DependencyResolver;
import org.apache.ivy.plugins.resolver.IBiblioResolver;
import org.marid.project.IdeProject;
import org.marid.project.ivy.event.IvyTransferEvent;
import org.marid.project.ivy.infrastructure.M2RepositoryExists;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Component
public class IvyContext {

  @Bean
  public EventManager eventManager(IdeProject project, GenericApplicationContext context) {
    final var eventManager = new EventManager();
    eventManager.addTransferListener(ev -> context.publishEvent(new IvyTransferEvent(context, project, ev)));
    return eventManager;
  }

  @Bean
  @Conditional(M2RepositoryExists.class)
  @Order(-2)
  public IBiblioResolver localMavenResolver() {
    final var resolver = new IBiblioResolver();
    resolver.setM2compatible(true);
    resolver.setUsepoms(true);
    resolver.setName("maven-local");
    resolver.setUseMavenMetadata(false);
    resolver.setRoot(M2RepositoryExists.REPO.toUri().toString());
    return resolver;
  }

  @Bean
  @Order(-1)
  public IBiblioResolver remoteMavenResolver() {
    final var resolver = new IBiblioResolver();
    resolver.setM2compatible(true);
    resolver.setUsepoms(true);
    resolver.setName("maven-central");
    return resolver;
  }

  @Bean
  public ResolveOptions resolveOptions() {
    final var options = new ResolveOptions();
    options.setTransitive(true);
    options.setDownload(true);
    options.setUseCacheOnly(false);
    options.setCheckIfChanged(true);
    return options;
  }

  @Bean
  public RetrieveOptions retrieveOptions() {
    final var options = new RetrieveOptions();
    options.setDestArtifactPattern("lib/[artifact]-[revision].[ext]");
    return options;
  }

  @Bean
  @Scope("ivy")
  public Ivy ivy(IdeProject project, EventManager eventManager, List<DependencyResolver> dependencyResolvers, IvyLogHandler logHandler) {
    final var ivy = new Ivy();

    ivy.setEventManager(eventManager);
    ivy.getLoggerEngine().setDefaultLogger(logHandler);

    ivy.bind();

    final var chainResolver = new ChainResolver();
    chainResolver.setReturnFirst(true);
    chainResolver.setName("main");
    dependencyResolvers.forEach(chainResolver::add);
    ivy.getSettings().addResolver(chainResolver);

    ivy.getSettings().setBaseDir(project.getIvyDirectory().toFile());
    ivy.getSettings().setDefaultCache(project.getIvyCacheDirectory().toFile());

    ivy.getSettings().getResolvers().stream()
        .filter(BasicResolver.class::isInstance)
        .map(BasicResolver.class::cast)
        .forEach(resolver -> resolver.setEventManager(eventManager));

    ivy.getSettings().setDefaultResolver("main");

    return ivy;
  }

  @Bean
  @Scope("ivy")
  public URLClassLoader ivyClassLoader(IdeProject project) throws IOException {
    final var list = new ArrayList<URL>();
    final var lib = project.getIvyDirectory().resolve("lib");
    if (Files.isDirectory(lib)) {
      try (final var stream = Files.newDirectoryStream(lib, "*.jar")) {
        for (final var file: stream) {
          list.add(file.toUri().toURL());
        }
      }
      return new URLClassLoader(list.toArray(URL[]::new), Thread.currentThread().getContextClassLoader());
    } else {
      return new URLClassLoader(new URL[0], Thread.currentThread().getContextClassLoader());
    }
  }
}
