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

import org.apache.maven.repository.internal.MavenRepositorySystemUtils
import org.eclipse.aether.RepositoryEvent
import org.eclipse.aether.RepositoryEvent.EventType.*
import org.eclipse.aether.RepositoryListener
import org.eclipse.aether.repository.LocalRepository
import org.eclipse.aether.transfer.MetadataNotFoundException
import org.eclipse.aether.transfer.TransferEvent
import org.eclipse.aether.transfer.TransferListener
import org.marid.fx.extensions.LOG
import org.marid.ide.extensions.bean
import org.marid.ide.project.Project
import org.marid.ide.project.dependencies.DependencyResolver
import org.springframework.beans.factory.ObjectFactory
import org.springframework.stereotype.Component
import java.lang.reflect.Proxy
import java.util.logging.Level.*
import java.util.logging.Logger

@Component
class ProjectSession(
  projectFactory: ObjectFactory<Project>,
  dependencyResolver: DependencyResolver
) {

  val project = projectFactory.bean
  private val logger = Logger.getLogger(project.id)

  val session = MavenRepositorySystemUtils.newSession()
    .apply {
      val classLoader = Thread.currentThread().contextClassLoader
      transferListener = Proxy.newProxyInstance(classLoader, arrayOf(TransferListener::class.java)) { _, _, a ->
        if (a.size == 1) {
          when (val arg = a[0]) {
            is TransferEvent -> {
              val (level, x) = when (arg.type) {
                TransferEvent.EventType.CORRUPTED -> WARNING to arg.exception
                TransferEvent.EventType.FAILED ->
                  if (arg.exception is MetadataNotFoundException)
                    if (arg.resource.repositoryUrl.startsWith("file://")) {
                      INFO to null
                    } else {
                      WARNING to null
                    }
                  else {
                    WARNING to arg.exception
                  }
                TransferEvent.EventType.PROGRESSED -> FINEST to null
                else -> INFO to null
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
                else -> INFO to null
              }
              logger.LOG(level, "{0}", x, arg)
            }
            else -> {
            }
          }
        }
      } as RepositoryListener
    }
    .apply {
      val localRepo = LocalRepository(project.cacheDepsDirectory.toFile())
      localRepositoryManager = dependencyResolver.repositorySystem.newLocalRepositoryManager(this, localRepo)
    }
}
