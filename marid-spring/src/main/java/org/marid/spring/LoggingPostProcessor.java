/*-
 * #%L
 * marid-spring
 * %%
 * Copyright (C) 2012 - 2019 MARID software development group
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

package org.marid.spring;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;

public class LoggingPostProcessor implements DestructionAwareBeanPostProcessor {

  private static final Log LOG = LogFactory.getLog(LoggingPostProcessor.class);

  @Override
  public Object postProcessBeforeInitialization(@Nullable Object bean, @Nullable String beanName) throws BeansException {
    if (beanName != null) {
      LOG.info("Initializing " + beanName);
    }
    return bean;
  }

  @Override
  public Object postProcessAfterInitialization(@Nullable Object bean, @Nullable String beanName) throws BeansException {
    if (beanName != null) {
      LOG.info("Initialized " + beanName + " " + bean);
    } else {
      LOG.info("Initialized " + bean);
    }
    return bean;
  }

  @Override
  public void postProcessBeforeDestruction(@Nullable Object bean, @Nullable String beanName) throws BeansException {
    LOG.info("Destroying " + beanName + " " + bean);
  }
}
