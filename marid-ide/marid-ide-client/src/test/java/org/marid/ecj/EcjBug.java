package org.marid.ecj;

/*-
 * #%L
 * marid-ide-client
 * %%
 * Copyright (C) 2012 - 2019 MARID software development group
 * %%
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 * 
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
import org.eclipse.jdt.internal.compiler.problem.DefaultProblemFactory;
import org.eclipse.jdt.internal.compiler.tool.EclipseCompiler;
import org.eclipse.jdt.internal.compiler.util.Util;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.*;
import java.io.*;
import java.net.URI;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import static java.nio.charset.StandardCharsets.UTF_8;

public class EcjBug {

  // Common

  private static final StringWriter OUTPUT = new StringWriter();

  // ECJ

  private static final CompilerOptions COMPILER_OPTIONS = compilerOptions();
  private static final FileSystem FILE_SYSTEM = fileSystem();
  private static final IErrorHandlingPolicy ERROR_HANDLING_POLICY = errorHandlingPolicy();
  private static final IProblemFactory PROBLEM_FACTORY = new DefaultProblemFactory(Locale.US);
  private static final ConcurrentHashMap<ICompilationUnit, CompilationUnitDeclaration> RESULTS = new ConcurrentHashMap<>();
  private static final ConcurrentLinkedQueue<CompilationResult> COMPILATION_RESULTS = new ConcurrentLinkedQueue<>();
  private static final ICompilerRequestor REQUESTOR = requestor(COMPILATION_RESULTS);
  private static final Compiler COMPILER = compiler(FILE_SYSTEM, COMPILER_OPTIONS, ERROR_HANDLING_POLICY, PROBLEM_FACTORY, REQUESTOR, OUTPUT, RESULTS);

  // JAVAC

  private static final JavaCompiler JAVA_COMPILER = ToolProvider.getSystemJavaCompiler();
  private static final DiagnosticCollector<JavaFileObject> DIAGNOSTIC_COLLECTOR = new DiagnosticCollector<>();
  private static final StandardJavaFileManager FILE_MANAGER = JAVA_COMPILER.getStandardFileManager(DIAGNOSTIC_COLLECTOR, Locale.US, UTF_8);

  // language=java
  private static final String CODE = "import java.lang.constant.Constable;\n" +
      "\n" +
      "public class TestFile {\n" +
      "\n" +
      "  @SafeVarargs\n" +
      "  public final <E> E elements(E... args) {\n" +
      "    return null;\n" +
      "  }\n" +
      "\n" +
      "  public void test1() {\n" +
      "    var v = elements(\"a\", 1);\n" +
      "  }\n" +
      "\n" +
      "  public void test2() {\n" +
      "    var v = elements(\"a\", (Comparable<String> & Constable) null);\n" +
      "  }\n" +
      "}";
  private static final JavaFileObject CODE_OBJECT = new SimpleJavaFileObject(URI.create("string://Test.java"), JavaFileObject.Kind.SOURCE) {
    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
      return CODE;
    }
  };

  // ENTRY-POINT: run your tests here

  public static void main(String... args) throws Exception {
    var target = Files.createTempDirectory("ecj");

    try {
      checkEcj(target);
    } finally {
      deleteRecursively(target);
    }

    target = Files.createTempDirectory("javac");

    try {
      checkJavac(target);
    } finally {
      deleteRecursively(target);
    }

    target = Files.createTempDirectory("ecj_via_stdapi");

    try {
      checkEcjViaStdApi(target);
    } finally {
      deleteRecursively(target);
    }
  }

  private static void checkEcj(Path target) {
    COMPILER.compile(new ICompilationUnit[]{
        new CompilationUnit(CODE.toCharArray(), "TestFile", "UTF-8", target.toString(), false, null)
    });
    final var unitDeclaration = RESULTS.values().iterator().next();

    final var methods = Arrays.stream(unitDeclaration.types[0].methods)
        .filter(m -> String.valueOf(m.binding.constantPoolName()).startsWith("test"))
        .sorted(Comparator.comparing(m -> String.valueOf(m.binding.constantPoolName())))
        .toArray(AbstractMethodDeclaration[]::new);

    System.out.printf("ecj type test1: %s%n", ((LocalDeclaration) methods[0].statements[0]).initialization.resolvedType);
    System.out.printf("ecj type test2: %s%n", ((LocalDeclaration) methods[1].statements[0]).initialization.resolvedType);
  }

  private static void checkJavac(Path target) throws Exception {
    FILE_MANAGER.setLocation(StandardLocation.CLASS_OUTPUT, List.of(target.toFile()));

    final var task = (JavacTask) JAVA_COMPILER.getTask(OUTPUT, FILE_MANAGER, DIAGNOSTIC_COLLECTOR, List.of("--release", "12"), null, List.of(CODE_OBJECT));

    final var parsed = task.parse();
    final var unit = parsed.iterator().next();

    task.analyze();

    final var trees = Trees.instance(task);
    final var classTree = (ClassTree) unit.getTypeDecls().get(0);

    final var method = classTree.getMembers().stream()
        .filter(MethodTree.class::isInstance)
        .map(MethodTree.class::cast)
        .filter(m -> m.getName().contentEquals("test2"))
        .findFirst()
        .orElseThrow();

    final var stats = method.getBody().getStatements();
    final var varTree = (VariableTree) stats.get(0);
    final var type = trees.getTypeMirror(trees.getPath(unit, varTree));

    System.out.printf("javac type: %s%n", type);
  }

  private static void checkEcjViaStdApi(Path target) throws Exception {
    final var compiler = new EclipseCompiler();
    final var fileManager = compiler.getStandardFileManager(DIAGNOSTIC_COLLECTOR, Locale.US, UTF_8);

    fileManager.setLocation(StandardLocation.CLASS_OUTPUT, List.of(target.toFile()));

    final var srcDir = Files.createTempDirectory("src");

    try {
      fileManager.setLocation(StandardLocation.SOURCE_PATH, List.of(srcDir.toFile()));
      Files.writeString(srcDir.resolve("TestFile.java"), CODE, UTF_8);
      final var javaFiles = fileManager.getJavaFileObjects(srcDir.resolve("TestFile.java"));

      final var options = List.of(
          "--release", "12",
          "-referenceInfo",
          "-parameters",
          "-preserveAllLocals"
      );
      final var task = compiler.getTask(OUTPUT, FILE_MANAGER, DIAGNOSTIC_COLLECTOR, options, null, javaFiles);
      task.setProcessors(List.of(new AbstractProcessor() {
        @Override
        public Set<String> getSupportedAnnotationTypes() {
          return Collections.emptySet();
        }

        @Override
        public SourceVersion getSupportedSourceVersion() {
          return SourceVersion.RELEASE_12;
        }

        @Override
        public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
          return true;
        }

        @Override
        public synchronized void init(ProcessingEnvironment processingEnv) {
          super.init(processingEnv);
          System.out.println(processingEnv);
        }
      }));
      final var compileStatus = task.call();
      System.out.printf("ecj via compiler api: %s%n", compileStatus);
    } finally {
      System.out.println(OUTPUT);
      deleteRecursively(srcDir);
    }
  }

  private static CompilerOptions compilerOptions() {
    final var compilerOptions = new CompilerOptions();
    compilerOptions.complianceLevel = compilerOptions.originalComplianceLevel = ClassFileConstants.JDK12;
    compilerOptions.sourceLevel = compilerOptions.originalSourceLevel = ClassFileConstants.JDK12;
    compilerOptions.targetJDK = ClassFileConstants.JDK12;
    compilerOptions.produceReferenceInfo = true;
    compilerOptions.preserveAllLocalVariables = true;
    compilerOptions.produceMethodParameters = true;
    compilerOptions.generateClassFiles = true;
    compilerOptions.processAnnotations = false;
    return compilerOptions;
  }

  private static FileSystem fileSystem() {
    final var classpath = new FileSystem.Classpath[]{
        new ClasspathJrt(new File(System.getProperty("java.home")), true, null, null)
    };
    return new FileSystem(classpath, new String[0], true) {
    };
  }

  private static IErrorHandlingPolicy errorHandlingPolicy() {
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

  private static ICompilerRequestor requestor(ConcurrentLinkedQueue<CompilationResult> compilationResults) {
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

  private static Compiler compiler(FileSystem fileSystem,
                                   CompilerOptions compilerOptions,
                                   IErrorHandlingPolicy errorHandlingPolicy,
                                   IProblemFactory problemFactory,
                                   ICompilerRequestor compilerRequestor,
                                   StringWriter output,
                                   ConcurrentHashMap<ICompilationUnit, CompilationUnitDeclaration> results) {
    return new Compiler(fileSystem, errorHandlingPolicy, compilerOptions, compilerRequestor, problemFactory, new PrintWriter(output), null) {
      @Override
      protected synchronized void addCompilationUnit(ICompilationUnit sourceUnit, CompilationUnitDeclaration parsedUnit) {
        results.put(sourceUnit, parsedUnit);
        super.addCompilationUnit(sourceUnit, parsedUnit);
      }
    };
  }

  private static void deleteRecursively(Path dir) throws IOException {
    Files.walkFileTree(dir, new SimpleFileVisitor<>() {
      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        Files.delete(file);
        return super.visitFile(file, attrs);
      }

      @Override
      public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        Files.delete(dir);
        return super.postVisitDirectory(dir, exc);
      }
    });
  }
}
