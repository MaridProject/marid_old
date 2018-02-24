/*-
 * #%L
 * marid-webapp
 * %%
 * Copyright (C) 2012 - 2018 MARID software development group
 * %%
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * #L%
 */

package org.marid.app.ui;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.JavaScriptExecutor;
import org.eclipse.rap.rwt.client.service.UrlLauncher;
import org.eclipse.swt.layout.GridLayout;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.GenericApplicationContext;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Configuration
public class UIBaseConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public UIContext uiContext() {
    return new UIBaseContext();
  }

  @Bean
  @Scope(SCOPE_PROTOTYPE)
  public UrlLauncher urlLauncher() {
    return RWT.getClient().getService(UrlLauncher.class);
  }

  @Bean
  @Scope(SCOPE_PROTOTYPE)
  public JavaScriptExecutor jsExecutor() {
    return RWT.getClient().getService(JavaScriptExecutor.class);
  }

  @Bean
  public DestructionAwareBeanPostProcessor loggingProcessor(GenericApplicationContext context) {
    final Logger logger = LoggerFactory.getLogger(context.getId());
    return new DestructionAwareBeanPostProcessor() {
      @Override
      public Object postProcessBeforeInitialization(@Nullable Object bean, @Nullable String beanName) {
        if (beanName != null) {
          logger.info("Initializing {}", beanName);
        }
        return bean;
      }

      @Override
      public void postProcessBeforeDestruction(@Nullable Object bean, @Nullable String beanName) {
        if (beanName != null) {
          logger.info("Destroyed {}", beanName);
        }
      }
    };
  }

  @Bean
  @ConditionalOnMissingBean
  public UIContextInitializer uiContextInitializer() {
    return context -> context.configure(((display, shell) -> {
      shell.setMaximized(true);
      final GridLayout layout = new GridLayout(1, false);
      layout.marginLeft = layout.marginRight = layout.marginTop = layout.marginBottom = 10;
      layout.verticalSpacing = 10;
      shell.setLayout(layout);
    }));
  }
}
