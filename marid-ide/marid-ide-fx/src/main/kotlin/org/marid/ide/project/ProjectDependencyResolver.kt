package org.marid.ide.project

import org.apache.maven.repository.internal.MavenRepositorySystemUtils
import org.eclipse.aether.RepositorySystem
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory
import org.eclipse.aether.impl.DefaultServiceLocator
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory
import org.eclipse.aether.spi.connector.transport.TransporterFactory
import org.eclipse.aether.transport.file.FileTransporterFactory
import org.eclipse.aether.transport.http.HttpTransporterFactory
import org.marid.fx.extensions.WARN
import org.marid.fx.extensions.logger
import org.springframework.stereotype.Component

@Component
class ProjectDependencyResolver {

  val repositorySystem: RepositorySystem = MavenRepositorySystemUtils.newServiceLocator()
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
}