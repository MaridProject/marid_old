package org.marid.ide.project

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.apache.maven.repository.internal.MavenRepositorySystemUtils
import org.eclipse.aether.RepositorySystem
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory
import org.eclipse.aether.impl.DefaultServiceLocator
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory
import org.eclipse.aether.spi.connector.transport.TransporterFactory
import org.eclipse.aether.transport.file.FileTransporterFactory
import org.eclipse.aether.transport.http.HttpTransporterFactory
import org.marid.fx.extensions.logger
import org.marid.fx.extensions.WARN
import org.marid.ide.common.Directories
import org.springframework.stereotype.Component
import java.nio.file.Files

@Component
class Projects(private val directories: Directories) {

  private val _items = FXCollections.observableArrayList(Project::observables)
  val items: ObservableList<Project> = FXCollections.unmodifiableObservableList(_items)

  init {
    Files.newDirectoryStream(directories.projectsHome) { Files.isDirectory(it) }.use { dirs ->
      for (dir in dirs) {
        _items += Project(this, dir.fileName.toString())
      }
    }
  }

  val repositorySystem = MavenRepositorySystemUtils.newServiceLocator()
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

  fun newProject(): Project {
    val project = Project(this)
    _items.add(project)
    return project
  }

  companion object {
    val Project.directories get() = projects.directories
    val Project.writableItems: ObservableList<Project> get() = projects._items
  }
}