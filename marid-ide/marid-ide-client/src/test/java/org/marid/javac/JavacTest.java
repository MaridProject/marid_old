package org.marid.javac;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.IntersectionTypeTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.JavacTask;
import com.sun.source.util.Trees;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
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
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("normal")
@ExtendWith({TestLogExtension.class, SpringExtension.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ContextConfiguration
class JavacTest {

  @Autowired
  private JavaCompiler javaCompiler;

  @Autowired
  private StandardJavaFileManager fileManager;

  @Autowired
  private DiagnosticCollector<JavaFileObject> collector;

  @Test
  void test() throws Exception {
    final var javaFile = Path.of(JavacTest.class.getResource("TestFile.java").toURI());
    final var srcs = fileManager.getJavaFileObjects(javaFile);
    final var writer = new StringWriter();
    final var task = (JavacTask) javaCompiler.getTask(writer, fileManager, collector, List.of("--release", "12"), null, srcs);
    final var parsed = task.parse();
    final var unit = parsed.iterator().next();
    task.analyze();
    final var classTree = (ClassTree) unit.getTypeDecls().get(0);
    final var method = classTree.getMembers().stream()
        .filter(MethodTree.class::isInstance)
        .map(MethodTree.class::cast)
        .filter(m -> m.getName().contentEquals("test1"))
        .findFirst()
        .orElseThrow();
    final var stats = method.getBody().getStatements();
    final var varTree = (VariableTree) stats.get(0);

    assertTrue(varTree.getType() instanceof IntersectionTypeTree);
    System.out.println(varTree.getType());
  }

  @Configuration
  static class JavacContext {

    @Bean
    JavaCompiler javaCompiler() {
      return ToolProvider.getSystemJavaCompiler();
    }

    @Bean
    DiagnosticCollector<JavaFileObject> diagnostics() {
      return new DiagnosticCollector<>();
    }

    @Bean
    Path srcFiles() throws Exception {
      return Path.of(JavacTest.class.getResource("TestFile.java").toURI()).getParent();
    }

    @Bean
    TempFolder newSrcFiles() {
      return new TempFolder("newFiles");
    }

    @Bean
    StandardJavaFileManager javaFileManager(JavaCompiler javaCompiler,
                                            DiagnosticCollector<JavaFileObject> diagnosticCollector,
                                            Path srcFiles,
                                            Path newSrcFiles) throws Exception {
      final var fileManager = javaCompiler.getStandardFileManager(diagnosticCollector, Locale.US, StandardCharsets.UTF_8);
      fileManager.setLocation(StandardLocation.SOURCE_PATH, List.of(srcFiles.toFile()));
      fileManager.setLocation(StandardLocation.SOURCE_OUTPUT, List.of(newSrcFiles.toFile()));
      return fileManager;
    }
  }
}
