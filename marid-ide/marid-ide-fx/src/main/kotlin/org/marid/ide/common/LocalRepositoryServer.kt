package org.marid.ide.common

import com.sun.net.httpserver.HttpServer
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
class LocalRepositoryServer : AutoCloseable {

  private lateinit var server: HttpServer
  private lateinit var executor: ForkJoinPool

  val m2RepoDir = Path.of(System.getProperty("user.home"), ".m2", "repository")
  val isEnabled = Files.isDirectory(m2RepoDir)

  init {
    if (isEnabled) {
      executor = ForkJoinPool(8)
      server = HttpServer.create(InetSocketAddress(0), 10)
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
  }

  val port: Int get() = if (isEnabled) server.address.port else 0
  val address: InetSocketAddress = if (isEnabled) InetSocketAddress(getLocalHost(), port) else InetSocketAddress(0)
  val url: URL = if (isEnabled) URL("http", "localhost", port, "/") else throw IllegalStateException("Not enabled")

  @PostConstruct
  fun start() {
    if (isEnabled) {
      server.start()
    }
  }

  override fun close() {
    if (isEnabled) {
      server.stop(0)
      executor.shutdown()
      if (!executor.awaitTermination(1L, TimeUnit.MINUTES)) {
        throw CancellationException()
      }
    }
  }
}