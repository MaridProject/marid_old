package org.marid.profile;

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

import org.marid.profile.exception.IdeProfileCloseException;
import org.marid.project.IdeProject;
import org.marid.project.IdeProjectContext;
import org.marid.spring.ContextUtils;
import org.marid.spring.events.ContextClosedListener;
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

  private final IdeProfileDirectory directory;
  private final GenericApplicationContext context;
  private final Path maridDirectory;
  private final Path projectsDirectory;
  private final ConcurrentHashMap<String, IdeProject> projects = new ConcurrentHashMap<>();

  public IdeProfile(IdeProfileDirectory directory, GenericApplicationContext context) throws IOException {
    this.directory = directory;
    this.context = context;
    this.maridDirectory = directory.getDirectory().resolve("marid");
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

  public IdeProject addProject(String name) {
    return projects.computeIfAbsent(name, projectName -> {
      final var directory = projectsDirectory.resolve(name);
      if (!directory.startsWith(this.directory.getDirectory())) {
        throw new IllegalArgumentException(name);
      }
      final var child = ContextUtils.context(context, (r, c) -> {
        r.register(IdeProjectContext.class);
        r.registerBean(Path.class, "ideProjectDirectory", () -> directory);
        c.getDefaultListableBeanFactory().registerSingleton("ideProjectDirectory", directory);
        c.addApplicationListener((ContextClosedListener) event -> projects.remove(projectName));
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
    return directory.getDirectory().hashCode();
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
    if (exception.getSuppressed().length > 0) {
      throw exception;
    }
  }
}
