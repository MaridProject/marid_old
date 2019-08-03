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
import org.apache.ivy.plugins.resolver.DependencyResolver;
import org.apache.ivy.plugins.resolver.FileSystemResolver;
import org.apache.ivy.plugins.resolver.IBiblioResolver;
import org.marid.project.IdeProject;
import org.marid.project.ivy.event.IvyTransferEvent;
import org.marid.project.ivy.infrastructure.M2RepositoryExists;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;
import java.util.HashSet;
import java.util.List;

import static org.marid.project.ivy.infrastructure.M2RepositoryExists.REPO;

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
  public FileSystemResolver localMavenResolver() {
    final var resolver = new FileSystemResolver();
    resolver.setM2compatible(true);
    resolver.setLocal(true);
    resolver.addArtifactPattern(REPO + "/[organisation]/[module]/[revision]/[module]-[revision](-[classifier]).[ext]");
    resolver.addIvyPattern(REPO + "/[organisation]/[module]/[revision]/[module]-[revision](-[classifier]).[ext]");
    resolver.setName("maven-local");
    return resolver;
  }

  @Bean
  public IBiblioResolver remoteMavenResolver() {
    final var resolver = new IBiblioResolver();
    resolver.setM2compatible(true);
    resolver.setUsepoms(true);
    resolver.setName("maven-central");
    return resolver;
  }

  @Bean
  @Scope("ivy")
  public Ivy ivy(IdeProject project,
                 EventManager eventManager,
                 List<DependencyResolver> dependencyResolvers,
                 IvyLogHandler logHandler) throws IOException, ParseException {
    final var ivy = new Ivy();

    ivy.setEventManager(eventManager);
    ivy.getLoggerEngine().setDefaultLogger(logHandler);

    final var ivyConfigFile = project.getIvyDirectory().resolve("ivy.xml");
    if (Files.isRegularFile(ivyConfigFile)) {
      ivy.configure(ivyConfigFile.toFile());
      final var resolverNames = new HashSet<>(ivy.getSettings().getResolverNames());
      dependencyResolvers.stream()
          .filter(r -> !resolverNames.contains(r.getName()))
          .forEach(ivy.getSettings()::addResolver);
    } else {
      ivy.bind();
      dependencyResolvers.forEach(ivy.getSettings()::addResolver);
    }

    return ivy;
  }
}
