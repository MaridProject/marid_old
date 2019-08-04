package org.marid.project;

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

import org.marid.io.MaridFiles;
import org.marid.profile.IdeProfile;
import org.marid.profile.event.RemoveProjectEvent;
import org.marid.spring.scope.ResettableScope;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;

import static java.lang.System.Logger.Level.ERROR;

@Component
public class IdeProject implements AutoCloseable {

  private final System.Logger logger;
  private final Path directory;
  private final Path ivyDirectory;
  private final Path ivyCacheDirectory;
  private final IdeProfile profile;
  private final GenericApplicationContext context;

  public IdeProject(IdeProfile profile, GenericApplicationContext context) throws Exception {
    this.profile = profile;

    final var name = context.getBean("ideProjectName", String.class);

    this.directory = profile.getProjectsDirectory().resolve(name);

    if (name.contains("/") || name.contains("\\") || !directory.startsWith(profile.getProjectsDirectory())) {
      throw new IllegalStateException("Invalid project name: " + name);
    }

    this.context = context;
    this.logger = System.getLogger(profile.getName() + "/" + name);

    this.ivyDirectory = directory.resolve("ivy");
    this.ivyCacheDirectory = ivyDirectory.resolve("cache");

    Files.createDirectories(ivyCacheDirectory);
  }

  public IdeProfile getProfile() {
    return profile;
  }

  public String getName() {
    return directory.getFileName().toString();
  }

  public Path getIvyDirectory() {
    return ivyDirectory;
  }

  public Path getIvyCacheDirectory() {
    return ivyCacheDirectory;
  }

  public void refresh() {
    final var beanFactory = context.getBeanFactory();
    final var scope = beanFactory.getRegisteredScope("ivy");
    if (scope instanceof ResettableScope) {
      try {
        ((ResettableScope) scope).reset();
      } catch (Throwable e) {
        logger.log(ERROR, () -> "Unable to refresh " + scope, e);
      }
    }
  }

  @Override
  public void close() {
    context.close();
  }

  @Override
  public int hashCode() {
    return directory.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return obj == this || obj instanceof IdeProject && ((IdeProject) obj).directory.equals(directory);
  }

  @Override
  public String toString() {
    return "Project(" + getName() + ")";
  }

  public void delete() {
    context.publishEvent(new RemoveProjectEvent(profile, this));

    Throwable contextException = null;
    try {
      context.close();
    } catch (Throwable e) {
      contextException = e;
    }

    try {
      MaridFiles.delete(directory);
    } catch (Throwable e) {
      if (contextException == null) {
        contextException = e;
      } else {
        contextException.addSuppressed(e);
      }
    }

    if (contextException != null) {
      if (contextException instanceof RuntimeException) {
        throw (RuntimeException) contextException;
      } else if (contextException instanceof Error) {
        throw (Error) contextException;
      } else {
        throw new IllegalStateException(contextException); // impossible
      }
    }
  }
}
