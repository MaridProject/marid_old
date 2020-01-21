package org.marid.ide.child.project

import javafx.concurrent.Service
import javafx.concurrent.Task
import org.marid.ide.main.IdeServices
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Component
class ProjectBuildService(private val services: IdeServices) : Service<Unit>() {

  override fun createTask(): Task<Unit> {
    return object : Task<Unit>() {
      override fun call() {
      }
    }
  }

  @PostConstruct
  fun onInit() {
    services.add(this)
  }

  @PreDestroy
  fun onDestroy() {
    services.remove(this)
  }
}