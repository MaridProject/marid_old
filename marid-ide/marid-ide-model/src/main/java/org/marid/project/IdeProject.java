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

import org.apache.ivy.Ivy;
import org.marid.io.MaridFiles;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class IdeProject implements AutoCloseable {

  private final Path directory;
  private final GenericApplicationContext context;
  private final Path ivyDirectory;

  private Ivy ivy = new Ivy();

  public IdeProject(IdeProjectDirectory directory, GenericApplicationContext context) throws Exception {
    this.directory = directory.getDirectory();
    this.context = context;
    this.ivyDirectory = directory.getDirectory().resolve("ivy");

    Files.createDirectories(ivyDirectory);
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
    return "Project(" + directory + ")";
  }

  public void delete() {
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
