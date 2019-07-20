package org.marid.ecj;

/*-
 * #%L
 * marid-types
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

import org.eclipse.jdt.internal.compiler.batch.FileSystem;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.marid.test.spring.TempFolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.nio.file.Path;
import java.util.stream.Stream;

@Tag("manual")
@ExtendWith(SpringExtension.class)
@ContextConfiguration
class EcjTest {

  @Autowired
  private Path classPathFolder1;

  @Test
  void testCompilation() {
    final var fileSystem = new FileSystem(new String[0], new String[] {"Test"}, "UTF-8");
  }

  @Configuration
  public static class TestContext {

    @Bean
    public TempFolder sourceFolder() {
      return new TempFolder("ecjtest");
    }

    @Bean
    public String[] classPathNames(@Qualifier("classpath") Path[] folders) {
      return Stream.of(folders)
          .map(Path::toAbsolutePath)
          .map(Path::toString)
          .toArray(String[]::new);
    }
  }
}
