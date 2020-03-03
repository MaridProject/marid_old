package org.marid.ide.child.project

import org.springframework.stereotype.Component
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentLinkedQueue

@Component
class ProjectScanner(private val buildService: ProjectBuildService) {

  fun allConstants(): List<Method> =
    buildService.withClassLoader { classLoader ->
      val queue = ConcurrentLinkedQueue<Method>()
      val url = classLoader.getResource(".")
      println(url)
      queue.toList()
    }
}