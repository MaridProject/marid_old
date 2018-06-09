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

import org.marid.applib.spring.events.ContextClosedListener;
import org.marid.applib.spring.events.ContextStartedListener;
import org.marid.applib.spring.init.InitBeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.function.Consumer;

public interface ContextUtils {

  @SafeVarargs
  static AnnotationConfigApplicationContext context(AbstractApplicationContext parent,
                                                    Consumer<AnnotationConfigApplicationContext>... configurers) {
    final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
    context.setAllowBeanDefinitionOverriding(false);
    context.setAllowCircularReferences(false);
    context.getBeanFactory().addBeanPostProcessor(new LoggingPostProcessor());
    context.getBeanFactory().addBeanPostProcessor(new InitBeanPostProcessor(context));

    context.setParent(parent);

    final var parentListener = closeListener(parent, event -> {
      try {
        context.close();
      } catch (Exception x) {
        x.printStackTrace();
      }
    });
    parent.addApplicationListener(parentListener);

    final var listener = closeListener(context, e -> parent.getApplicationListeners().remove(parentListener));
    context.addApplicationListener(listener);

    for (final Consumer<AnnotationConfigApplicationContext> configurer : configurers) {
      configurer.accept(context);
    }

    return context;
  }

  static ContextClosedListener closeListener(ApplicationContext context, ContextClosedListener listener) {
    return ev -> {
      if (ev.getApplicationContext() == context) {
        listener.onApplicationEvent(ev);
      }
    };
  }

  static ContextStartedListener startListener(ApplicationContext context, ContextStartedListener listener) {
    return ev -> {
      if (ev.getApplicationContext() == context) {
        listener.onApplicationEvent(ev);
      }
    };
  }
}
