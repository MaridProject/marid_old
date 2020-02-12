package org.marid.fx.extensions

import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import org.marid.fx.action.Fx
import org.marid.fx.action.configure
import org.marid.fx.i18n.localized

fun MenuBar.menu(text: String): Menu = Menu()
  .also {
    it.textProperty().bind(text.localized)
    menus += it
  }

fun Menu.menu(text: String): Menu = Menu()
  .also {
    it.textProperty().bind(text.localized)
    items += it
  }

fun Menu.menu(action: Fx): Menu = Menu()
  .also {
    it.configure(action)
    items += it
  }

fun Menu.item(action: Fx): MenuItem = MenuItem()
  .also {
    it.configure(action)
    items += it
  }