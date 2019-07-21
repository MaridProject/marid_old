package org.marid.runtime.model;

/*-
 * #%L
 * marid-runtime
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

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("normal")
class UrlTest {

  @ParameterizedTest
  @CsvSource(value = {
      "https://localhost:8443/file1.zip|file1",
      "jar:http://host/file.zip!/file/file/file2.zip|file2",
      "https://localhost:8443/x/file1.zip?o=1|file1",
      "file:///x/1.zip|1",
      "http://x/1 2.zip|1_2"
  }, delimiter = '|')
  void testPath(String urlText, String name) throws Exception {
    final var url = new URL(urlText);
    final var actual = WineBusiness.name(url);

    assertEquals(name, actual);
  }
}
