package org.marid.fx.extensions

import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import org.marid.fx.i18n.localized

fun <MB : MenuBar> MB.menu(text: String): Menu = Menu().also { it.textProperty().bind(text.localized); menus += it }