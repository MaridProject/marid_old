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

package org.marid.spring;

import org.marid.spring.events.ContextClosedListener;
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import java.util.function.BiConsumer;

public interface ContextUtils {

  @SafeVarargs
  static GenericApplicationContext context(AbstractApplicationContext parent,
                                           BiConsumer<AnnotatedBeanDefinitionReader, GenericApplicationContext>... cs) {
    final var context = new GenericApplicationContext();
    final var bdReader = new AnnotatedBeanDefinitionReader(context);

    final var beanFactory = context.getDefaultListableBeanFactory();
    beanFactory.setAllowBeanDefinitionOverriding(false);
    beanFactory.setAllowCircularReferences(false);

    beanFactory.addBeanPostProcessor(new LoggingPostProcessor());

    // do not use context.setParent(...) due to child-to-parent event propagation
    beanFactory.setParentBeanFactory(parent.getBeanFactory());

    final var parentApplicationListeners = parent.getApplicationListeners();
    final var parentListener = (ContextClosedListener) event -> context.close();
    parent.addApplicationListener(parentListener);
    context.addApplicationListener((ContextClosedListener) e -> parentApplicationListeners.remove(parentListener));

    for (final var configurer : cs) {
      configurer.accept(bdReader, context);
    }

    return context;
  }
}
