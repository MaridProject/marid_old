/*-
 * #%L
 * marid-ide-fx
 * %%
 * Copyright (C) 2012 - 2019 MARID software development group
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

package org.marid.ide

import javafx.application.Application
import javafx.stage.Stage
import org.marid.ide.log.IdeLog
import org.marid.image.MaridIconFx
import org.marid.logging.MaridConsoleLogHandler
import org.marid.logging.MaridLogFormatter
import org.marid.logging.MaridLogManager
import org.marid.spring.LoggingPostProcessor
import org.marid.spring.init.InitBeanPostProcessor
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import java.util.function.Supplier
import java.util.logging.LogManager

class IdeApp : Application() {

  private val context = AnnotationConfigApplicationContext()

  override fun init() {
    initLogging()
    with(context) {
      setAllowCircularReferences(false)
      setAllowBeanDefinitionOverriding(false)
      defaultListableBeanFactory.addBeanPostProcessor(LoggingPostProcessor())
      defaultListableBeanFactory.addBeanPostProcessor(InitBeanPostProcessor(context))
      register(IdeContext::class.java)
    }
  }

  override fun start(primaryStage: Stage) {
    with(primaryStage) {
      title = "Marid IDE"
      isMaximized = true
      icons.setAll(*MaridIconFx.getIcons(22, 24, 32))
    }
    with(context) {
      setUserAgentStylesheet(STYLESHEET_MODENA)
      defaultListableBeanFactory.registerSingleton("primaryStage", primaryStage)
      defaultListableBeanFactory.registerSingleton("application", this@IdeApp)
      refresh()
      start()
      primaryStage.show()
    }
  }

  override fun stop() {
    context.close()
  }

  private fun initLogging() {
    System.setProperty("java.util.logging.manager", MaridLogManager::class.java.name)
    LogManager.getLogManager().reset()
    val ideLog = IdeLog()
    context.registerBean(IdeLog::class.java, Supplier { ideLog })
    with(IdeLog.rootLogger()) {
      addHandler(MaridConsoleLogHandler().also { it.formatter = MaridLogFormatter() })
    }
  }
}
