package org.marid.fx.extensions

import javafx.event.ActionEvent
import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import org.marid.fx.action.FxAction
import org.marid.fx.action.configure
import org.marid.fx.i18n.localized

fun <MB : MenuBar> MB.menu(text: String): Menu = Menu()
  .also { it.textProperty().bind(text.localized); menus += it }

fun <M: Menu> M.item(text: String, icon: String, handler: (ActionEvent) -> Unit): MenuItem = item(
  FxAction(text = text, icon = icon, handler = handler)
)

fun <M: Menu> M.item(text: String, handler: (ActionEvent) -> Unit): MenuItem = item(
  FxAction(text = text, handler = handler)
)

fun <M: Menu> M.item(action: FxAction): MenuItem = MenuItem()
  .also { it.configure(action) }
  .also { items += it }