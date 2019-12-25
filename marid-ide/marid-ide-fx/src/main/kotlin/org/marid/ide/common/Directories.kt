package org.marid.ide.common

import org.springframework.stereotype.Component
import java.nio.file.Files
import java.nio.file.Path

@Component
class Directories {

  val userHome = Path.of(System.getProperty("user.home"))
  val maridHome = userHome.resolve("marid").also { Files.createDirectories(it) }
  val projectsHome = maridHome.resolve("projects").also { Files.createDirectories(it) }
}