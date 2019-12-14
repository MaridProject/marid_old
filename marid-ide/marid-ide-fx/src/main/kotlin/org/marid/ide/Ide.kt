package org.marid.ide

import javafx.application.Application

object Ide {
  @JvmStatic fun main(args: Array<String>) = Application.launch(IdeApp::class.java, *args)
}