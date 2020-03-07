package org.marid.ide.child.project

import org.marid.fx.extensions.*
import org.marid.runtime.annotation.Constant
import org.marid.runtime.annotation.Constants
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
import javax.annotation.PreDestroy

@Component
class ProjectScanner(private val buildService: ProjectBuildService) {

  private val executor = ThreadPoolExecutor(0, 16, 1L, SECONDS, SynchronousQueue(), CallerRunsPolicy())

  fun allConstants(): List<Method> =
    buildService.withClassLoader { classLoader ->
      val constantsAnnotation = classLoader.loadClass(Constants::class.java.name).asSubclass(Annotation::class.java)
      val constantAnnotation = classLoader.loadClass(Constant::class.java.name).asSubclass(Annotation::class.java)
      try {
        classLoader.urLs.flatMap { url ->
          logger.INFO("Loading from {0}", url)
          JarFile(File(url.toURI()), false).use { f ->
            f.stream()
              .filter { it.name.endsWith(".class") && !it.name.contains('$') && !it.name.contains('-') }
              .map { it.name.substring(0, it.name.length - 6).replace('/', '.') }
              .tryMap({ classLoader.loadClass(it) }) { v, _ -> logger.WARN("Unable to load {0} from {1}", v, url) }
              .filter { it.isAnnotationPresent(constantsAnnotation) }
              .flatMap { Arrays.stream(it.methods).filter { m -> m.isAnnotationPresent(constantAnnotation) } }
              .collect(Collectors.toUnmodifiableList())
          }
        }
      } catch (e: Throwable) {
        logger.ERROR("Unable to load constants", e)
        emptyList()
      }
    }

  @PreDestroy
  private fun destroy() {
    executor.shutdown()
  }
}