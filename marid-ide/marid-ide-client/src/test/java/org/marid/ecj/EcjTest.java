package org.marid.ecj;

/*-
 * #%L
 * marid-ide-client
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

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.JavacTask;
import com.sun.source.util.Trees;
import org.eclipse.jdt.internal.compiler.Compiler;
import org.eclipse.jdt.internal.compiler.*;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
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
import org.eclipse.jdt.internal.compiler.util.Util;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.marid.test.logging.TestLogExtension;
import org.marid.test.spring.TempFolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.lang.model.type.IntersectionType;
import javax.tools.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("normal")
@ExtendWith({TestLogExtension.class, SpringExtension.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ContextConfiguration
class EcjTest {

  @Autowired
  private ConcurrentHashMap<ICompilationUnit, CompilationUnitDeclaration> resultMap;

  @Autowired
  private StringWriter output;

  @Autowired
  private Path targetFolder;

  @Autowired
  private Supplier<Stream<Path>> javaFiles;

  @Autowired
  private Compiler compiler;

  @Autowired
  private JavaCompiler systemCompiler;

  @Autowired
  private StandardJavaFileManager fileManager;

  @Autowired
  private DiagnosticCollector<JavaFileObject> collector;

  @Test
  @Order(2)
  void testTypeInferring() throws IOException {
    final var files = javaFiles.get().toArray(Path[]::new);
    final var units = new ICompilationUnit[files.length];
    for (int i = 0; i < files.length; i++) {
      final var contents = Files.readString(files[i], StandardCharsets.UTF_8).toCharArray();
      final var name = files[i].getFileName().toString();
      final var target = targetFolder.toString();

      units[i] = new CompilationUnit(contents, name, "UTF-8", target, false, null);
    }

    compiler.compile(units);

    assertEquals(1, resultMap.size());
    final var unitDeclaration = resultMap.values().iterator().next();
    assertEquals(1, unitDeclaration.types.length);
    final var methods = Arrays.stream(unitDeclaration.types[0].methods)
        .filter(m -> String.valueOf(m.binding.constantPoolName()).startsWith("test"))
        .sorted(Comparator.comparing(m -> String.valueOf(m.binding.constantPoolName())))
        .toArray(AbstractMethodDeclaration[]::new);

    {
      assertEquals(1, methods[0].statements.length);
      assertTrue(methods[0].statements[0] instanceof LocalDeclaration);
      final var localDeclaration = (LocalDeclaration) methods[0].statements[0];
      final var localBinding = localDeclaration.binding;
      assertTrue(localBinding.type instanceof IntersectionTypeBinding18);
    }

    {
      assertEquals(1, methods[2].statements.length);
      assertTrue(methods[2].statements[0] instanceof LocalDeclaration);
      final var localDeclaration = (LocalDeclaration) methods[2].statements[0];
      final var localBinding = localDeclaration.binding;

      assertTrue(localBinding.type instanceof IntersectionTypeBinding18);
    }
  }

  @Test
  @Order(3)
  void testTypeInferring2() throws IOException {
    final var files = javaFiles.get().toArray(Path[]::new);
    final var units = fileManager.getJavaFileObjects(files);
    final var task = (JavacTask) systemCompiler.getTask(output, fileManager, collector, List.of(), List.of(), units);

    final var parsed = task.parse();
    final var unit = parsed.iterator().next();

    task.analyze();

    final var trees = Trees.instance(task);

    assertEquals(1, unit.getTypeDecls().size());
    final var classTree = (ClassTree) unit.getTypeDecls().get(0);

    final var method = classTree.getMembers().stream()
        .filter(MethodTree.class::isInstance)
        .map(MethodTree.class::cast)
        .filter(m -> m.getName().contentEquals("test2"))
        .findFirst()
        .orElseThrow();

    final var stats = method.getBody().getStatements();
    assertEquals(1, stats.size());
    assertTrue(stats.get(0) instanceof VariableTree);

    final var varTree = (VariableTree) stats.get(0);

    final var type = trees.getTypeMirror(trees.getPath(unit, varTree));
    assertTrue(type instanceof IntersectionType);
  }

  @Configuration
  public static class TestContext {

    @Bean
    public Path sourceFolder() throws Exception {
      return Path.of(EcjTest.class.getResource("TestFile.java").toURI()).getParent();
    }

    @Bean
    public TempFolder targetFolder() {
      return new TempFolder("ecjtest");
    }

    @Bean
    public TempFolder targetFolderJavac() {
      return new TempFolder("ecjtestjavac");
    }

    @Bean
    public Supplier<Stream<Path>> javaFiles(Path sourceFolder) throws Exception {
      final var files = Files.find(sourceFolder, 3, (p, a) -> p.getFileName().toString().endsWith(".java")).toArray(Path[]::new);
      return () -> Arrays.stream(files);
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
      compilerOptions.complianceLevel = compilerOptions.originalComplianceLevel = ClassFileConstants.JDK12;
      compilerOptions.sourceLevel = compilerOptions.originalSourceLevel = ClassFileConstants.JDK12;
      compilerOptions.targetJDK = ClassFileConstants.JDK12;
      compilerOptions.produceReferenceInfo = true;
      compilerOptions.preserveAllLocalVariables = true;
      compilerOptions.produceMethodParameters = true;
      compilerOptions.generateClassFiles = true;
      compilerOptions.processAnnotations = false;
      compilerOptions.enablePreviewFeatures = true;
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
    public ICompilerRequestor requestor(ConcurrentLinkedQueue<CompilationResult> compilationResults) {
      return result -> {
        compilationResults.add(result);
        for (final var classFile : result.getClassFiles()) {
          if (result.compilationUnit.getDestinationPath() != null) {
            try {
              final var dest = result.compilationUnit.getDestinationPath();
              final var name = String.valueOf(classFile.fileName()) + ".class";
              Util.writeToDisk(true, dest, name, classFile);
            } catch (IOException e) {
              throw new UncheckedIOException(e);
            }
          }
        }
      };
    }

    @Bean
    public Compiler compiler(FileSystem fileSystem,
                             CompilerOptions compilerOptions,
                             IErrorHandlingPolicy errorHandlingPolicy,
                             IProblemFactory problemFactory,
                             ICompilerRequestor compilerRequestor,
                             StringWriter output,
                             ConcurrentHashMap<ICompilationUnit, CompilationUnitDeclaration> resultMap) {
      return new Compiler(fileSystem, errorHandlingPolicy, compilerOptions, compilerRequestor, problemFactory, new PrintWriter(output), null) {
        @Override
        protected synchronized void addCompilationUnit(ICompilationUnit sourceUnit, CompilationUnitDeclaration parsedUnit) {
          resultMap.put(sourceUnit, parsedUnit);
          super.addCompilationUnit(sourceUnit, parsedUnit);
        }
      };
    }

    @Bean
    public JavaCompiler systemJavaCompiler() {
      return ToolProvider.getSystemJavaCompiler();
    }

    @Bean
    public DiagnosticCollector<JavaFileObject> collector() {
      return new DiagnosticCollector<>();
    }

    @Bean
    public StandardJavaFileManager fileManager(Path targetFolderJavac, DiagnosticCollector<JavaFileObject> collector, JavaCompiler systemJavaCompiler) throws IOException {
      final var fileManager = systemJavaCompiler.getStandardFileManager(collector, Locale.US, StandardCharsets.UTF_8);
      fileManager.setLocation(StandardLocation.CLASS_OUTPUT, List.of(targetFolderJavac.toFile()));
      return fileManager;
    }
  }
}
