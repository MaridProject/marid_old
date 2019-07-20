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

import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.Compiler;
import org.eclipse.jdt.internal.compiler.IErrorHandlingPolicy;
import org.eclipse.jdt.internal.compiler.IProblemFactory;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.batch.ClasspathJrt;
import org.eclipse.jdt.internal.compiler.batch.CompilationUnit;
import org.eclipse.jdt.internal.compiler.batch.FileSystem;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.lookup.IntersectionTypeBinding18;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblemFactory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.marid.test.spring.TempFolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("manual")
@ExtendWith({SpringExtension.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ContextConfiguration
class EcjTest {

  @Autowired
  private ConcurrentHashMap<ICompilationUnit, CompilationUnitDeclaration> resultMap;

  @Autowired
  private StringWriter output;

  @Autowired
  private Compiler compiler;

  @Test
  @Order(1)
  void testCompilation() {
    assertEquals("", output.getBuffer().toString());
  }

  @Test
  @Order(2)
  void testTypeInferring() {
    assertEquals(1, resultMap.size());
    final var unitDeclaration = resultMap.values().iterator().next();
    assertEquals(1, unitDeclaration.types.length);
    final var method = Arrays.stream(unitDeclaration.types[0].methods)
        .filter(m -> String.valueOf(m.binding.constantPoolName()).equals("test1"))
        .findFirst()
        .orElseThrow();
    assertEquals(1, method.statements.length);
    assertTrue(method.statements[0] instanceof LocalDeclaration);
    final var localDeclaration = (LocalDeclaration) method.statements[0];
    final var localBinding = localDeclaration.binding;
    assertTrue(localBinding.type instanceof IntersectionTypeBinding18);
  }

  @Test
  @Order(3)
  void testHugeMethod() {
    final var builder = new StringBuilder("class X {\n  public void m() {\n");
    for (int i = 0; i < 10_000; i++) {
      builder.append(String.format("    var x%d = java.util.Arrays.asList(1, 'a'); \n", i));
    }
    builder.append("  }\n");
    builder.append("}\n");
    compiler.compile(new ICompilationUnit[]{
        new CompilationUnit(builder.toString().toCharArray(), "X.java", "UTF-8", null, false, null)
    });
    assertEquals("", output.toString());
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
    public Supplier<Stream<Path>> javaFiles(Path sourceFolder) throws Exception {
      final var files = Files.find(sourceFolder, 3, (p, a) -> p.getFileName().toString().endsWith(".java")).toArray(Path[]::new);
      return () -> Arrays.stream(files);
    }

    @Bean
    public Supplier<Stream<ICompilationUnit>> compilationUnits(Supplier<Stream<Path>> javaFiles, Path targetFolder) throws IOException {
      final var files = javaFiles.get().toArray(Path[]::new);
      final var units = new ICompilationUnit[files.length];
      for (int i = 0; i < files.length; i++) {
        final var contents = Files.readString(files[i], StandardCharsets.UTF_8).toCharArray();
        final var name = files[i].getFileName().toString();
        final var target = targetFolder.toString();

        units[i] = new CompilationUnit(contents, name, "UTF-8", target, false, null);
      }
      return () -> Arrays.stream(units);
    }

    @Bean
    public FileSystem fileSystem(Path[] javaFiles) {
      final var classpath = new FileSystem.Classpath[]{
          new ClasspathJrt(new File(System.getProperty("java.home")), true, null, null)
      };
      return new FileSystem(classpath, Stream.of(javaFiles).map(p -> p.getFileName().toString()).toArray(String[]::new), true) {
      };
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
    public IProblemFactory problemFactory() {
      return new DefaultProblemFactory(Locale.US);
    }

    @Bean
    public ConcurrentLinkedQueue<CompilationResult> compilationResults() {
      return new ConcurrentLinkedQueue<>();
    }

    @Bean
    public ConcurrentHashMap<ICompilationUnit, CompilationUnitDeclaration> resultsMap() {
      return new ConcurrentHashMap<>();
    }

    @Bean
    public StringWriter output() {
      return new StringWriter();
    }

    @Bean
    public IErrorHandlingPolicy errorHandlingPolicy() {
      return new IErrorHandlingPolicy() {
        @Override
        public boolean proceedOnErrors() {
          return false;
        }

        @Override
        public boolean stopOnFirstError() {
          return false;
        }

        @Override
        public boolean ignoreAllErrors() {
          return false;
        }
      };
    }

    @Bean
    public Compiler compiler(FileSystem fileSystem,
                             CompilerOptions compilerOptions,
                             IErrorHandlingPolicy errorHandlingPolicy,
                             IProblemFactory problemFactory,
                             ConcurrentLinkedQueue<CompilationResult> results,
                             StringWriter output,
                             ConcurrentHashMap<ICompilationUnit, CompilationUnitDeclaration> resultMap) {
      return new Compiler(fileSystem, errorHandlingPolicy, compilerOptions, results::add, problemFactory, new PrintWriter(output), null) {
        @Override
        protected synchronized void addCompilationUnit(ICompilationUnit sourceUnit, CompilationUnitDeclaration parsedUnit) {
          resultMap.put(sourceUnit, parsedUnit);
          super.addCompilationUnit(sourceUnit, parsedUnit);
        }
      };
    }

    @Bean(initMethod = "run")
    public Runnable compilationTask(Compiler compiler, Supplier<Stream<ICompilationUnit>> units) {
      return () -> compiler.compile(units.get().toArray(ICompilationUnit[]::new));
    }
  }
}
