package org.marid.ide.client;

import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.zip.ZipInputStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class CopyFiles {

  public static void main(String... args) throws Exception {
    final var location = CopyFiles.class.getProtectionDomain().getCodeSource().getLocation();
    final var dir = Paths.get(location.toURI()).getParent().getParent()
        .resolve("src")
        .resolve("main")
        .resolve("binary-resources")
        .resolve("META-INF")
        .resolve("resources")
        .resolve("public")
        .resolve("webix");

    if (Files.isDirectory(dir)) {
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

    final var webixDistro = new URL("https://webix.com/packages/webix.zip");
    try (final var zip = new ZipInputStream(webixDistro.openStream(), UTF_8)) {
      for (var entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
        try {
          if (entry.isDirectory() || entry.getName().startsWith("samples")) {
            continue;
          }
          final var name = entry.getName();
          final var path = name.startsWith("codebase/") ? name.substring("codebase/".length()) : name;
          final var target = dir.resolve(path);
          final var parent = target.getParent();
          Files.createDirectories(parent);
          Files.copy(zip, target, REPLACE_EXISTING);
          System.out.printf("Copied %s%n", entry.getName());
        } finally {
          zip.closeEntry();
        }
      }
    }
  }
}
