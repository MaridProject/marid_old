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

package org.marid.ide.common

import com.sun.net.httpserver.HttpServer
import org.springframework.context.annotation.Condition
import org.springframework.context.annotation.ConditionContext
import org.springframework.context.annotation.Conditional
import org.springframework.core.type.AnnotatedTypeMetadata
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import java.net.HttpURLConnection
import java.net.InetAddress.getLocalHost
import java.net.InetSocketAddress
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.CancellationException
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.TimeUnit
import javax.annotation.PostConstruct

@Component
@Conditional(LocalRepositoryServerCondition::class)
class LocalRepositoryServer : AutoCloseable {

  private val server = HttpServer.create(InetSocketAddress(0), 10)
  private val executor: ForkJoinPool = ForkJoinPool(8)

  val m2RepoDir = Path.of(System.getProperty("user.home"), ".m2", "repository")

  init {
    server.executor = executor
    server.createContext("/") { e ->
      try {
        var path = e.requestURI.path.substring(1)
        if (path.endsWith("/maven-metadata.xml")) {
          val index = path.lastIndexOf('/')
          path = path.substring(0, index) + "/maven-metadata-local.xml"
        }
        val file = path.split("/").fold(m2RepoDir, Path::resolve)
        if (Files.isRegularFile(file)) {
          val contentType = when (val ext = StringUtils.getFilenameExtension(file.fileName.toString())) {
            "pom", "xml" -> "text/xml"
            "jar" -> "application/java-archive"
            else -> throw IllegalStateException("Unknown extension: $ext")
          }
          e.responseHeaders.add("Content-Type", contentType)
          e.sendResponseHeaders(HttpURLConnection.HTTP_OK, Files.size(file))
          e.responseBody.use { Files.copy(file, it) }
        } else {
          e.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, 0L)
        }
      } catch (x: Throwable) {
        e.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, -1L)
        x.printStackTrace()
      }
    }
  }

  val port: Int get() = server.address.port
  val address: InetSocketAddress = InetSocketAddress(getLocalHost(), port)
  val url: URL = URL("http", "localhost", port, "/")

  @PostConstruct
  fun start() {
    server.start()
  }

  override fun close() {
    server.stop(0)
    executor.shutdown()
    if (!executor.awaitTermination(1L, TimeUnit.MINUTES)) {
      throw CancellationException()
    }
  }
}

class LocalRepositoryServerCondition : Condition {
  override fun matches(context: ConditionContext, metadata: AnnotatedTypeMetadata): Boolean {
    return Files.isDirectory(Path.of(System.getProperty("user.home"), ".m2", "repository"))
  }
}
