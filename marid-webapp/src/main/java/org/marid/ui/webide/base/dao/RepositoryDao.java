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

import org.marid.applib.repository.RepositoryProvider;
import org.marid.collections.MaridIterators;
import org.marid.ui.webide.base.UserDirectories;
import org.marid.ui.webide.base.model.RepositoryItem;
import org.marid.ui.webide.base.model.RepositoryProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.TreeMap;

import static java.util.stream.Collectors.toMap;

@Component
public class RepositoryDao {

  private final Path directory;

  public RepositoryDao(UserDirectories directories) {
    directory = directories.getRepositoriesDirectory();
  }

  public List<RepositoryItem> repositories() {
    try (final DirectoryStream<Path> files = Files.newDirectoryStream(directory, "*.properties")) {
      final var paths = MaridIterators.array(Path.class, files);
      final var repositories = new RepositoryItem[paths.length];
      for (int i = 0; i < paths.length; i++) {
        try (final var inputStream = Files.newInputStream(paths[i])) {
          final var props = new Properties();
          props.load(inputStream);
          final var selector = (String) props.remove("selector");
          if (selector == null) {
            continue;
          }
          final var name = StringUtils.stripFilenameExtension(paths[i].getFileName().toString());
          final var repo = new RepositoryItem(selector, name);
          for (final var k : props.stringPropertyNames()) {
            repo.getProperties().add(new RepositoryProperty(k, props.getProperty(k)));
          }
          repositories[i] = repo;
        }
      }
      return List.of(repositories);
    } catch (NoSuchFileException x) {
      return List.of();
    } catch (IOException x) {
      throw new UncheckedIOException(x);
    }
  }

  public void remove(RepositoryItem repositoryItem) {
    try {
      Files.deleteIfExists(directory.resolve(repositoryItem.getName() + ".properties"));
    } catch (IOException x) {
      throw new UncheckedIOException(x);
    }
  }

  public void save(RepositoryItem repositoryItem) {
    try {
      final var file = directory.resolve(repositoryItem.getName() + ".properties");
      final var props = new Properties(repositoryItem.getProperties().size());
      repositoryItem.getProperties().forEach(p -> props.setProperty(p.getKey(), p.getValue()));
      props.setProperty("selector", repositoryItem.getSelector());
      try (final var stream = new PrintStream(Files.newOutputStream(file), false, StandardCharsets.UTF_8)) {
        props.list(stream);
      }
    } catch (IOException x) {
      throw new UncheckedIOException(x);
    }
  }

  public TreeMap<String, RepositoryProvider> selectors() {
    return ServiceLoader.load(RepositoryProvider.class).stream()
        .map(Provider::get)
        .collect(toMap(RepositoryProvider::getName, p -> p, (v1, v2) -> v2, TreeMap::new));
  }

  public TreeMap<String, String> selectorsMap() {
    return ServiceLoader.load(RepositoryProvider.class).stream()
        .map(Provider::get)
        .collect(toMap(RepositoryProvider::getName, RepositoryProvider::getDescription, (v1, v2) -> v2, TreeMap::new));
  }
}
