package org.marid.ide.fx;

/*-
 * #%L
 * marid-ide-fx
 * %%
 * Copyright (C) 2012 - 2020 MARID software development group
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

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.Locale;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.singleton;
import static javax.tools.StandardLocation.CLASS_OUTPUT;
import static javax.tools.StandardLocation.SOURCE_PATH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("normal")
class JavacTest {

  @Test
  void compileToMemory() throws Exception {
    try (final var fs = Jimfs.newFileSystem(Configuration.unix())) {
      final var compiler = ToolProvider.getSystemJavaCompiler();
      final var diagnostics = new DiagnosticCollector<JavaFileObject>();
      final var fileManager = compiler.getStandardFileManager(diagnostics, Locale.getDefault(), UTF_8);
      final var sourceDir = fs.getPath("/src");
      final var targetDir = fs.getPath("/target");
      Files.createDirectories(sourceDir);
      Files.createDirectories(targetDir);
      fileManager.setLocationFromPaths(SOURCE_PATH, singleton(sourceDir));
      fileManager.setLocationFromPaths(CLASS_OUTPUT, singleton(targetDir));
      final var source = sourceDir.resolve("Test.java");
      final var sourceObject = fileManager.getJavaFileObjects(source).iterator().next();
      assertEquals(JavaFileObject.Kind.SOURCE, sourceObject.getKind());
      Files.writeString(source, "public class Test { public int m() { return 1; } }", UTF_8);
      final var output = new StringWriter();
      final var task = compiler.getTask(output, fileManager, diagnostics, null, null, singleton(sourceObject));
      if (task.call()) {
        final var testClassFile = targetDir.resolve("Test.class");
        assertTrue(Files.isRegularFile(testClassFile));
        try (final var classLoader = new URLClassLoader(new URL[]{targetDir.toUri().toURL()})) {
          final var clazz = classLoader.loadClass("Test");
          final var instance = clazz.getConstructor().newInstance();
          final var method = clazz.getMethod("m");
          final var result = method.invoke(instance);
          assertEquals(1, result);
        }
      } else {
        throw new IllegalStateException(output.toString());
      }
    }
  }
}
