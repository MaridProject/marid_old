/*-
 * #%L
 * marid-webapp
 * %%
 * Copyright (C) 2012 - 2018 MARID software development group
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
package org.marid.ui.webide.base.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.marid.applib.repository.Artifact;
import org.marid.ui.webide.base.UserDirectories;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

@Component
public class ArtifactDao {

  private final Path directory;
  private final ObjectMapper mapper;

  public ArtifactDao(UserDirectories userDirectories, ObjectMapper mapper) {
    this.directory = userDirectories.getRepositoriesDirectory();
    this.mapper = mapper;
  }

  public List<Artifact> loadArtifacts() {
    final Path artifacts = directory.resolve("artifacts.list");
    try (final var reader = Files.newBufferedReader(artifacts, UTF_8)) {
      final var parser = mapper.getFactory().createParser(reader);
      return mapper.readValues(parser, Artifact.class).readAll();
    } catch (NoSuchFileException x) {
      return List.of();
    } catch (IOException x) {
      throw new UncheckedIOException(x);
    }
  }

  public void save(Iterable<Artifact> artifacts) {
    final Path file = directory.resolve("artifacts.list");
    try (final Writer writer = Files.newBufferedWriter(file, UTF_8)) {
      mapper.writerFor(Artifact.class).writeValues(writer).writeAll(artifacts);
    } catch (IOException x) {
      throw new UncheckedIOException(x);
    }
  }
}
