/*-
 * #%L
 * marid-webapp
 * %%
 * Copyright (C) 2012 - 2018 MARID software development group
 * %%
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * #L%
 */
package org.marid.ui.webide.base.dao;

import org.marid.applib.dao.ListDao;
import org.marid.applib.model.ProjectItem;
import org.marid.io.MaridFiles;
import org.marid.ui.webide.base.UserDirectories;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ProjectDao implements ListDao<String, ProjectItem> {

  private final Path directory;

  public ProjectDao(UserDirectories userDirectories) {
    this.directory = userDirectories.getProjectsDirectory();
  }

  @Override
  public void add(ProjectItem item) {
    try {
      final Path path = directory.resolve(item.getId());
      Files.createDirectories(path);
    } catch (IOException x) {
      throw new UncheckedIOException(x);
    }
  }

  @Override
  public void remove(String id) {
    try {
      final Path path = directory.resolve(id);
      if (Files.isDirectory(path)) {
        FileSystemUtils.deleteRecursively(path);
      }
    } catch (IOException x) {
      throw new UncheckedIOException(x);
    }
  }

  @Override
  public void update(ProjectItem item) {
  }

  @Override
  public List<ProjectItem> get() {
    try {
      return Files.list(directory)
          .filter(Files::isDirectory)
          .map(p -> new ProjectItem(p.getFileName().toString()))
          .collect(Collectors.toUnmodifiableList());
    } catch (NoSuchFileException x) {
      return List.of();
    } catch (IOException x) {
      throw new UncheckedIOException(x);
    }
  }

  @Override
  public Set<String> getIds() {
    try {
      return Files.list(directory)
          .filter(Files::isDirectory)
          .map(p -> p.getFileName().toString())
          .collect(Collectors.toUnmodifiableSet());
    } catch (NoSuchFileException x) {
      return Set.of();
    } catch (IOException x) {
      throw new UncheckedIOException(x);
    }
  }

  @Override
  public Optional<ProjectItem> get(String name) {
    final Path dir = directory.resolve(name);
    return Optional.of(dir)
        .filter(Files::isDirectory)
        .map(d -> new ProjectItem(d.getFileName().toString()));
  }

  public long getSize(String id) {
    return MaridFiles.size(directory.resolve(id));
  }
}
