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

package org.marid.applib.spring;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;

public class LoggingPostProcessor implements DestructionAwareBeanPostProcessor {

  private final Logger logger = LoggerFactory.getLogger("beans");

  @Override
  public Object postProcessBeforeInitialization(@Nullable Object bean, @Nullable String beanName) throws BeansException {
    if (beanName != null) {
      logger.info("Initializing {}", beanName);
    }
    return bean;
  }

  @Override
  public Object postProcessAfterInitialization(@Nullable Object bean, @Nullable String beanName) throws BeansException {
    if (beanName != null) {
      logger.info("Initialized {} {}", beanName, bean);
    } else {
      logger.info("Initialized {}", bean);
    }
    return bean;
  }

  @Override
  public void postProcessBeforeDestruction(@Nullable Object bean, @Nullable String beanName) throws BeansException {
    logger.info("Destroying {} {}", beanName, bean);
  }
}
