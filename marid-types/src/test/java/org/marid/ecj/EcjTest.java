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

import org.eclipse.jdt.core.compiler.CompilationProgress;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.Compiler;
import org.eclipse.jdt.internal.compiler.IErrorHandlingPolicy;
import org.eclipse.jdt.internal.compiler.IProblemFactory;
import org.eclipse.jdt.internal.compiler.batch.FileSystem;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblemFactory;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.marid.test.spring.TempFolder;
import org.mockito.Mockito;
import org.mockito.internal.creation.MockSettingsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Locale;
import java.util.stream.Stream;

@Tag("manual")
@ExtendWith(SpringExtension.class)
@ContextConfiguration
class EcjTest {

  @Autowired
  private ArrayList<CompilationResult> results;

  @Test
  void testCompilation() {
  }

  @Configuration
  public static class TestContext {

    @Bean
    public Path sourceFolder() throws Exception {
      return Path.of(EcjTest.class.getResource("TestFile.java").toURI());
    }

    @Bean
    public TempFolder targetFolder() {
      return new TempFolder("ecjtest");
    }

    @Bean
    public Path[] javaFiles(Path sourceFolder) throws Exception {
      return Files.find(sourceFolder, 3, (p, a) -> p.getFileName().toString().endsWith(".java")).toArray(Path[]::new);
    }

    @Bean
    public FileSystem fileSystem(Path[] javaFiles) {
      return new FileSystem(new String[0], Stream.of(javaFiles).map(p -> p.getFileName().toString()).toArray(String[]::new), "UTF-8");
    }

    @Bean
    public IErrorHandlingPolicy errorHandlingPolicy() {
      final var handlingPolicy = Mockito.mock(IErrorHandlingPolicy.class, new MockSettingsImpl<>().stubOnly());
      Mockito.when(handlingPolicy.stopOnFirstError()).thenReturn(true);
      return handlingPolicy;
    }

    @Bean
    public CompilerOptions compilerOptions() {
      final var compilerOptions = new CompilerOptions();
      compilerOptions.complianceLevel = compilerOptions.originalComplianceLevel = ClassFileConstants.JDK11;
      compilerOptions.sourceLevel = compilerOptions.originalSourceLevel = ClassFileConstants.JDK11;
      compilerOptions.targetJDK = ClassFileConstants.JDK11;
      compilerOptions.produceReferenceInfo = true;
      compilerOptions.preserveAllLocalVariables = true;
      compilerOptions.produceMethodParameters = true;
      compilerOptions.generateClassFiles = false;
      return compilerOptions;
    }

    @Bean
    public ArrayList<CompilationResult> compilationResults() {
      return new ArrayList<>();
    }

    @Bean
    public IProblemFactory problemFactory() {
      return new DefaultProblemFactory(Locale.US);
    }

    @Bean
    public StringWriter output() {
      return new StringWriter();
    }

    @Bean
    public Compiler compiler(FileSystem fileSystem,
                             IErrorHandlingPolicy errorHandlingPolicy,
                             CompilerOptions compilerOptions,
                             ArrayList<CompilationResult> compilationResults,
                             IProblemFactory problemFactory,
                             StringWriter output) {

      return new Compiler(
          fileSystem,
          errorHandlingPolicy,
          compilerOptions,
          compilationResults::add,
          problemFactory,
          new PrintWriter(output),
          Mockito.mock(CompilationProgress.class, new MockSettingsImpl<>().stubOnly())
      );
    }

    @Bean(initMethod = "run")
    public Runnable compilationTask(Compiler compiler, Path targetFolder) throws IOException {
      return () -> {
      };
    }
  }
}
