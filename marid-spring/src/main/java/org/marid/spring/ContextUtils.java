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

import org.marid.spring.events.BroadcastEvent;
import org.marid.spring.events.ForwardingEvent;
import org.marid.spring.events.PropagatedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import java.util.function.BiConsumer;

public interface ContextUtils {

  static GenericApplicationContext context(
    AbstractApplicationContext parent,
    BiConsumer<AnnotatedBeanDefinitionReader, GenericApplicationContext> configurer
  ) {
    final var context = new GenericApplicationContext();
    final var beanDefinitionReader = new AnnotatedBeanDefinitionReader(context);

    final var beanFactory = context.getDefaultListableBeanFactory();
    beanFactory.setAllowBeanDefinitionOverriding(false);
    beanFactory.setAllowCircularReferences(false);

    // do not use context.setParent(...) due to child-to-parent event propagation
    beanFactory.setParentBeanFactory(parent.getBeanFactory());

    final var parentApplicationListeners = parent.getApplicationListeners();
    final ApplicationListener<ApplicationEvent> parentListener = event -> {
      if (event instanceof ContextClosedEvent) {
        context.close();
      } else if (event instanceof ForwardingEvent<?>) {
        context.publishEvent(event);
      } else if (event instanceof BroadcastEvent<?>) {
        final var broadcastEvent = (BroadcastEvent<?>) event;
        if (broadcastEvent.check(context)) {
          context.publishEvent(broadcastEvent);
        }
      }
    };
    parent.addApplicationListener(parentListener);

    context.addApplicationListener(event -> {
      if (event instanceof ContextClosedEvent) {
        parentApplicationListeners.remove(parentListener);
      } else if (event instanceof PropagatedEvent<?>) {
        parent.publishEvent(event);
      } else if (event instanceof BroadcastEvent<?>) {
        final var broadcastEvent = (BroadcastEvent<?>) event;
        if (broadcastEvent.check(parent)) {
          parent.publishEvent(broadcastEvent);
        }
      }
    });

    configurer.accept(beanDefinitionReader, context);

    return context;
  }
}
