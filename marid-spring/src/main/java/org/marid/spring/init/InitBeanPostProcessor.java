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
package org.marid.spring.init;

import org.marid.spring.annotation.SpringComponent;
import org.marid.spring.events.ContextStartedListener;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Comparator.comparing;
import static java.util.logging.Level.WARNING;
import static org.marid.logging.Log.log;

public class InitBeanPostProcessor implements BeanPostProcessor {

  private static final ClassValue<Map<String, Integer>> METHOD_ORDERS = new ClassValue<>() {
    @Override
    protected Map<String, Integer> computeValue(Class<?> type) {
      final var name = type.getSimpleName() + ".methods";

      try (final var inputStream = type.getResourceAsStream(name)) {

        if (inputStream == null) {
          log(WARNING, "Unable to find {0}", name);
          return Map.of();
        }

        try (final var scanner = new Scanner(inputStream, UTF_8)) {
          return IntStream.range(0, Integer.MAX_VALUE)
              .takeWhile(i -> scanner.hasNextLine())
              .mapToObj(i -> Map.entry(scanner.nextLine(), i))
              .filter(e -> !e.getKey().isEmpty())
              .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
        }
      } catch (IOException x) {
        throw new UncheckedIOException(x);
      }
    }
  };

  private final GenericApplicationContext context;

  public InitBeanPostProcessor(GenericApplicationContext context) {
    this.context = context;
  }

  @Override
  public Object postProcessAfterInitialization(@Nullable Object bean, String beanName) throws BeansException {
    if (bean == null || !bean.getClass().isAnnotationPresent(SpringComponent.class)) {
      return bean;
    }
    try {
      initialize(bean);
    } catch (Exception x) {
      throw new BeanInitializationException("Unable to initialize " + beanName, x);
    }
    return bean;
  }

  private void initialize(Object bean) throws Exception {
    final Map<String, Integer> orderMap = METHOD_ORDERS.get(bean.getClass());
    final var methods = Stream.of(bean.getClass().getMethods())
        .filter(m -> !Modifier.isStatic(m.getModifiers()))
        .filter(m -> m.canAccess(bean))
        .filter(m -> m.isAnnotationPresent(Init.class))
        .sorted(comparing(m -> orderMap.getOrDefault(m.getName(), 0)))
        .collect(Collectors.toUnmodifiableList());

    if (methods.isEmpty()) {
      return;
    }

    final var beanFactory = context.getDefaultListableBeanFactory();

    if (bean.getClass().isAnnotationPresent(InitAfterStart.class)) {
      context.addApplicationListener(new ContextStartedListener() {
        @Override
        public void onApplicationEvent(@NonNull ContextStartedEvent event) {
          context.getApplicationListeners().remove(this);
          try {
            invoke(bean, beanFactory, methods);
          } catch (Exception x) {
            throw new IllegalStateException(x);
          }
        }
      });
    } else {
      invoke(bean, beanFactory, methods);
    }
  }

  private void invoke(Object bean, DefaultListableBeanFactory beanFactory, List<Method> methods) throws Exception {
    for (final var method : methods) {
      final var args = IntStream.range(0, method.getParameterCount())
          .mapToObj(i -> new DependencyDescriptor(new MethodParameter(method, i), true, true))
          .map(dd -> beanFactory.resolveDependency(dd, null))
          .toArray();
      method.invoke(bean, args);
    }
  }
}
