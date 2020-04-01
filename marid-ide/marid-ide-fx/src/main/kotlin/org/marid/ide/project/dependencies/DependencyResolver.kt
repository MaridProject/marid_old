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

package org.marid.ide.project.dependencies

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
class DependencyResolver {

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
