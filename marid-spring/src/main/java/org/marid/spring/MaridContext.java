package org.marid.spring;

/*-
 * #%L
 * marid-spring
 * %%
 * Copyright (C) 2012 - 2018 MARID software development group
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

import org.marid.spring.events.ContextClosedListener;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

public class MaridContext extends AnnotationConfigApplicationContext {

  public MaridContext(AbstractApplicationContext parent) {
    getDefaultListableBeanFactory().setAllowBeanDefinitionOverriding(false);
    getDefaultListableBeanFactory().setAllowCircularReferences(false);
    getDefaultListableBeanFactory().addBeanPostProcessor(new LoggingPostProcessor());
    getDefaultListableBeanFactory().setParentBeanFactory(parent.getBeanFactory());

    final var parentApplicationListeners = parent.getApplicationListeners();
    final var parentListener = (ContextClosedListener) event -> close();
    parent.addApplicationListener(parentListener);
    addApplicationListener((ContextClosedListener) e -> parentApplicationListeners.remove(parentListener));
  }
}
