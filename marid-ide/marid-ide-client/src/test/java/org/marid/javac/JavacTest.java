package org.marid.javac;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.marid.test.logging.TestLogExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.tools.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Locale;

@Tag("normal")
@ExtendWith({TestLogExtension.class, SpringExtension.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class JavacTest {

  @TempDir
  Path tempDir;

  @Autowired
  private JavaCompiler javaCompiler;

  @Test
  void test() {
    var compiler = ToolProvider.getSystemJavaCompiler();
    var diagnostics = new DiagnosticCollector<JavaFileObject>();
    var fileManager = compiler.getStandardFileManager(diagnostics, Locale.US, StandardCharsets.UTF_8);

  }

  @Configuration
  static class JavacTestContext {

    @Bean
    JavaCompiler javaCompiler() {
      return ToolProvider.getSystemJavaCompiler();
    }

    @Bean
    DiagnosticCollector<JavaFileObject> diagnostics() {
      return new DiagnosticCollector<>();
    }

    @Bean
    StandardJavaFileManager javaFileManager(JavaCompiler javaCompiler, DiagnosticCollector<JavaFileObject> diagnosticCollector) {
      return javaCompiler.getStandardFileManager(diagnosticCollector, Locale.US, StandardCharsets.UTF_8);
    }
  }
}
