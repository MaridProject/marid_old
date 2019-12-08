package org.marid.ide

import javafx.application.Application

object IdeLauncher {
  @JvmStatic fun main(args: Array<String>) = Application.launch(Ide::class.java, *args)
}