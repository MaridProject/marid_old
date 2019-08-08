package org.marid.profile;

/*-
 * #%L
 * marid-ide-model
 * %%
 * Copyright (C) 2012 - 2019 MARID software development group
 * %%
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 * 
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 * #L%
 */

import org.marid.profile.event.AddProjectEvent;
import org.marid.profile.exception.IdeProfileCloseException;
import org.marid.project.IdeProject;
import org.marid.project.IdeProjectContext;
import org.marid.spring.ContextUtils;
import org.marid.spring.scope.ResettableScope;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.System.Logger.Level.INFO;
import static java.lang.System.Logger.Level.WARNING;

@Component
public final class IdeProfile implements AutoCloseable {

  private static final System.Logger LOGGER = System.getLogger(IdeProfile.class.getName());

  private final Path directory;
  private final GenericApplicationContext context;
  private final Path maridDirectory;
  private final Path projectsDirectory;
  private final ConcurrentHashMap<String, IdeProject> projects = new ConcurrentHashMap<>();

  public IdeProfile(GenericApplicationContext context) throws IOException {
    this.directory = context.getBean("ideProfileDirectory", Path.class);
    this.context = context;
    this.maridDirectory = directory.resolve("marid");
    this.projectsDirectory = maridDirectory.resolve("projects");

    Files.createDirectories(projectsDirectory);

    try (final var projectDirs = Files.newDirectoryStream(projectsDirectory, Files::isDirectory)) {
      for (final var projectDir : projectDirs) {
        try {
          addProject(projectDir.getFileName().toString());
        } catch (Throwable e) {
          LOGGER.log(WARNING, "Unable to add project {0}", projectDir);
        }
      }
    }
  }

  public String getName() {
    return directory.getFileName().toString();
  }

  public Path getMaridDirectory() {
    return maridDirectory;
  }

  public Path getProjectsDirectory() {
    return projectsDirectory;
  }

  public IdeProject addProject(String name) {
    return projects.computeIfAbsent(name, projectName -> {
      final var directory = projectsDirectory.resolve(name);
      if (!directory.startsWith(this.directory)) {
        throw new IllegalArgumentException(name);
      }
      final var child = ContextUtils.context(context, (r, c) -> {
        r.register(IdeProjectContext.class);
        c.getDefaultListableBeanFactory().registerSingleton("ideProjectName", projectName);
        c.getDefaultListableBeanFactory().registerScope("ivy", new ResettableScope(getName() + "/" + projectName));
        c.addApplicationListener(event -> {
          if (event instanceof ContextClosedEvent) {
            projects.remove(projectName);
          } else if (event instanceof ContextRefreshedEvent) {
            c.publishEvent(new AddProjectEvent(this, c.getBean(IdeProject.class)));
          }
        });
      });
      child.refresh();
      child.start();
      return child.getBean(IdeProject.class);
    });
  }

  public boolean removeProject(String name) {
    final var project = projects.remove(name);
    if (project != null) {
      project.delete();
      return true;
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return directory.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return obj == this || obj instanceof IdeProfile && ((IdeProfile) obj).directory.equals(directory);
  }

  @Override
  public String toString() {
    return directory.toString();
  }

  @Override
  public void close() {
    final var exception = new IdeProfileCloseException(this);
    projects.forEach((name, project) -> {
      try (project) {
        LOGGER.log(INFO, "Closing {0}", project);
      } catch (Throwable e) {
        exception.addSuppressed(e);
      }
    });
    try {
      context.close();
    } catch (Throwable e) {
      exception.addSuppressed(e);
    }
    if (exception.getSuppressed().length > 0) {
      throw exception;
    }
  }
}
