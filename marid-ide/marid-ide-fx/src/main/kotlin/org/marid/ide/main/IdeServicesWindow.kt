package org.marid.ide.main

import javafx.concurrent.Service
import javafx.event.EventHandler
import javafx.event.WeakEventHandler
import javafx.scene.Scene
import javafx.scene.control.TableView
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.stage.Window
import javafx.stage.WindowEvent
import org.marid.fx.extensions.column
import org.marid.fx.extensions.readOnlyProp
import org.marid.spring.annotation.PrototypeScoped
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.support.GenericApplicationContext
import org.springframework.stereotype.Component

@PrototypeScoped
@Component
class IdeServicesWindow(private val statusBar: IdeStatusBar, services: IdeServices) : Stage(StageStyle.UNDECORATED) {

  private val table = TableView<Service<*>>(services.services)
    .apply {
      columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
      column(200, "Name") { services.id(it).readOnlyProp }
    }

  private val parentHiddenHandler = EventHandler<WindowEvent> { close() }
  private val weakParentHiddenHandler = WeakEventHandler(parentHiddenHandler)

  init {
    initOwner(statusBar.scene.window)
    scene = Scene(table, 800.0, 400.0)
    statusBar.scene.window.addEventFilter(WindowEvent.WINDOW_HIDING, weakParentHiddenHandler)
  }

  @Autowired
  fun init(context: GenericApplicationContext) {
    if (Window.getWindows().any { it is IdeServicesWindow }) {
      return
    }
    addEventFilter(WindowEvent.WINDOW_HIDDEN) { context.defaultListableBeanFactory.destroyBean(this) }
    val bounds = statusBar.localToScreen(statusBar.boundsInLocal)
    show()
    x = bounds.maxX - width
    y = bounds.minY - height
  }
}