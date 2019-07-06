package org.marid.app.config;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class TemplatingConfiguration {

  @Bean
  public VelocityEngine velocityEngine() {
    final var engine = new VelocityEngine();
    return engine;
  }
}
