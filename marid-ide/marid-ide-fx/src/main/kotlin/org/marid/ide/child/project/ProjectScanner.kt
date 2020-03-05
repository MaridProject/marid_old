package org.marid.ide.child.project

import org.marid.fx.extensions.INFO
import org.marid.fx.extensions.WARN
import org.marid.fx.extensions.logger
import org.springframework.stereotype.Component
import java.io.File
import java.lang.reflect.Method
import java.util.*
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy
import java.util.concurrent.TimeUnit.SECONDS
import java.util.jar.JarFile
import java.util.stream.Collectors
import java.util.stream.Stream
import javax.annotation.PreDestroy

@Component
class ProjectScanner(private val buildService: ProjectBuildService) {

  private val executor = ThreadPoolExecutor(0, 16, 1L, SECONDS, SynchronousQueue(), CallerRunsPolicy())

  fun allConstants(): List<Method> =
    buildService.withClassLoader { classLoader ->
      try {
        classLoader.urLs.flatMap { url ->
          logger.INFO("Loading from {0}", url)
          JarFile(File(url.toURI()), false).use { f ->
            f.stream()
              .filter { it.name.endsWith(".class") }
              .filter { !it.name.contains('$') && !it.name.contains('-') }
              .map { it.name.substring(0, it.name.length - 6).replace('/', '.') }
              .flatMap {
                try {
                  Stream.of(classLoader.loadClass(it))
                } catch (_: Throwable) {
                  logger.WARN("Unable to load {0} from {1}", it, url)
                  Stream.empty<Class<*>>()
                }
              }
              .filter {
                it.annotations.any { a -> a.annotationClass.simpleName == "Constants" }
              }
              .flatMap {
                Arrays.stream(it.methods)
                  .filter { m ->
                    m.annotations.any { a -> a.annotationClass.simpleName == "Constant" }
                  }
              }
              .collect(Collectors.toUnmodifiableList())
          }
        }
      } catch (e: Throwable) {
        e.printStackTrace()
        throw e
      }
    }

  @PreDestroy
  private fun destroy() {
    executor.shutdown()
  }
}