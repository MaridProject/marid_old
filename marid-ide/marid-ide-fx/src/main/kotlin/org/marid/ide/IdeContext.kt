package org.marid.ide

import javafx.stage.Stage
import org.marid.ide.stage.PrimaryStage
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan
open class IdeContext {

  @Suppress("SpringJavaInjectionPointsAutowiringInspection")
  @Bean open fun primaryStage(_primaryStage_: Stage): PrimaryStage = PrimaryStage(_primaryStage_)
}