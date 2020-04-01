/*-
 * #%L
 * marid-ide-fx
 * %%
 * Copyright (C) 2012 - 2020 MARID software development group
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

package org.marid.ide.child.project

import org.marid.fx.extensions.*
import org.marid.runtime.annotation.Constant
import org.marid.runtime.annotation.Constants
import org.marid.runtime.annotation.Rack
import org.springframework.stereotype.Component
import java.io.File
import java.lang.reflect.Constructor
import java.lang.reflect.Method
import java.util.*
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy
import java.util.concurrent.TimeUnit.SECONDS
import java.util.jar.JarFile
import java.util.stream.Stream
import javax.annotation.PreDestroy

@Component
class ProjectScanner(private val buildService: ProjectBuildService) {

  private val executor = ThreadPoolExecutor(0, 16, 1L, SECONDS, SynchronousQueue(), CallerRunsPolicy())

  fun constants(): List<Method> =
    buildService.withClassLoader { classLoader ->
      val constantsAnnotation = classLoader.loadClass(Constants::class.java.name).asSubclass(Annotation::class.java)
      val constantAnnotation = classLoader.loadClass(Constant::class.java.name).asSubclass(Annotation::class.java)
      try {
        Arrays.stream(classLoader.urLs).parallel()
          .flatMap { url ->
            logger.INFO("Loading from {0}", url)
            jarClassStream(JarFile(File(url.toURI()), false), classLoader)
              .filter { it.isAnnotationPresent(constantsAnnotation) }
              .flatMap { Arrays.stream(it.methods).filter { m -> m.isAnnotationPresent(constantAnnotation) } }
          }
          .toImmutableList()
      } catch (e: Throwable) {
        logger.ERROR("Unable to load constants", e)
        emptyList()
      }
    }

  fun racks(): List<Constructor<*>> =
    buildService.withClassLoader { classLoader ->
      val rackAnnotation = classLoader.loadClass(Rack::class.qualifiedName).asSubclass(Annotation::class.java)
      try {
        Arrays.stream(classLoader.urLs).parallel()
          .flatMap { url ->
            logger.INFO("Loading from {0}", url)
            jarClassStream(JarFile(File(url.toURI()), false), classLoader)
              .filter { it.isAnnotationPresent(rackAnnotation) }
              .flatMap { Arrays.stream(it.constructors) }
          }
          .toImmutableList()
      } catch (e: Throwable) {
        logger.ERROR("Unable to load racks", e)
        emptyList()
      }
    }

  private fun jarClassStream(f: JarFile, classLoader: ClassLoader): Stream<Class<*>> = f.stream().onClose(f::close)
    .filter { it.name.endsWith(".class") && !it.name.contains('$') && !it.name.contains('-') }
    .map { it.name.substring(0, it.name.length - 6).replace('/', '.') }
    .tryMap({ classLoader.loadClass(it) }) { v, _ -> logger.WARN("Unable to load {0} from {1}", v, f.name) }

  @PreDestroy
  private fun destroy() {
    executor.shutdown()
  }
}
