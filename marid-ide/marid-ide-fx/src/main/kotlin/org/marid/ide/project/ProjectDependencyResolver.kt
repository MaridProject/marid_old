package org.marid.ide.project

import org.apache.maven.repository.internal.MavenRepositorySystemUtils
import org.apache.maven.repository.internal.MavenRepositorySystemUtils.newSession
import org.eclipse.aether.DefaultRepositorySystemSession
import org.eclipse.aether.RepositoryEvent
import org.eclipse.aether.RepositoryEvent.EventType.*
import org.eclipse.aether.RepositoryListener
import org.eclipse.aether.RepositorySystem
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory
import org.eclipse.aether.impl.DefaultServiceLocator
import org.eclipse.aether.repository.LocalRepository
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory
import org.eclipse.aether.spi.connector.transport.TransporterFactory
import org.eclipse.aether.transfer.MetadataNotFoundException
import org.eclipse.aether.transfer.TransferEvent
import org.eclipse.aether.transfer.TransferEvent.EventType.*
import org.eclipse.aether.transfer.TransferListener
import org.eclipse.aether.transport.file.FileTransporterFactory
import org.eclipse.aether.transport.http.HttpTransporterFactory
import org.marid.fx.extensions.LOG
import org.marid.fx.extensions.WARN
import org.marid.fx.extensions.logger
import org.springframework.stereotype.Component
import java.lang.reflect.Proxy
import java.nio.file.Files
import java.nio.file.Path
import java.util.logging.Level
import java.util.logging.Level.WARNING

@Component
class ProjectDependencyResolver {

  private val repositorySystem: RepositorySystem = MavenRepositorySystemUtils.newServiceLocator()
    .apply { addService(RepositoryConnectorFactory::class.java, BasicRepositoryConnectorFactory::class.java) }
    .apply { addService(TransporterFactory::class.java, HttpTransporterFactory::class.java) }
    .apply { addService(TransporterFactory::class.java, FileTransporterFactory::class.java) }
    .apply {
      setErrorHandler(object : DefaultServiceLocator.ErrorHandler() {
        override fun serviceCreationFailed(type: Class<*>, impl: Class<*>, exception: Throwable) {
          logger.WARN("Service {0}:{1} creation failed", exception, type.name, impl.name)
        }
      })
    }
    .run { getService(RepositorySystem::class.java) }

  fun <R> withSession(callback: (DefaultRepositorySystemSession, RepositorySystem) -> R): R = newSession()
    .apply {
      val local = Path.of(System.getProperty("user.home"), ".m2", "repository")
      if (Files.isDirectory(local)) {
        val repo = LocalRepository(local.toFile())
        localRepositoryManager = repositorySystem.newLocalRepositoryManager(this, repo)
      }
    }
    .apply {
      val classLoader = Thread.currentThread().contextClassLoader
      transferListener = Proxy.newProxyInstance(classLoader, arrayOf(TransferListener::class.java)) { _, _, a ->
        if (a.size == 1) {
          when (val arg = a[0]) {
            is TransferEvent -> {
              val (level, x) = when (arg.type) {
                CORRUPTED -> WARNING to arg.exception
                FAILED ->
                  if (arg.exception is MetadataNotFoundException)
                    WARNING to null
                  else
                    WARNING to arg.exception
                PROGRESSED -> Level.OFF to null
                else -> Level.INFO to null
              }
              logger.LOG(level, "{0}", x, arg)
            }
            else -> {
            }
          }
        }
      } as TransferListener
      repositoryListener = Proxy.newProxyInstance(classLoader, arrayOf(RepositoryListener::class.java)) { _, _, a ->
        if (a.size == 1) {
          when (val arg = a[0]) {
            is RepositoryEvent -> {
              val (level, x) = when (arg.type) {
                ARTIFACT_DESCRIPTOR_INVALID, ARTIFACT_DESCRIPTOR_MISSING, METADATA_INVALID -> WARNING to arg.exception
                else -> Level.INFO to null
              }
              logger.LOG(level, "{0}", x, arg)
            }
            else -> {
            }
          }
        }
      } as RepositoryListener
    }
    .run { callback(this, repositorySystem) }
}