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

import org.marid.applib.repository.Artifact;
import org.marid.ui.webide.base.UserDirectories;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.marid.applib.json.MaridJackson.MAPPER;

@Component
public class ArtifactDao {

  private final Path directory;

  public ArtifactDao(UserDirectories userDirectories) {
    this.directory = userDirectories.getRepositoriesDirectory();
  }

  public List<Artifact> loadArtifacts() {
    final var artifacts = directory.resolve("artifacts.list");
    try (final var reader = Files.newBufferedReader(artifacts, UTF_8)) {
      final var parser = MAPPER.getFactory().createParser(reader);
      return MAPPER.readValues(parser, Artifact.class).readAll();
    } catch (NoSuchFileException x) {
      return List.of();
    } catch (IOException x) {
      throw new UncheckedIOException(x);
    }
  }

  public void save(Iterable<Artifact> artifacts) {
    final var file = directory.resolve("artifacts.list");
    try (final var writer = Files.newBufferedWriter(file, UTF_8)) {
      MAPPER.writerFor(Artifact.class).writeValues(writer).writeAll(artifacts);
    } catch (IOException x) {
      throw new UncheckedIOException(x);
    }
  }
}
