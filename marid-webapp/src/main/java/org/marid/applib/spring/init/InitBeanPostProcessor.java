/*-
 * #%L
 * marid-webapp
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
package org.marid.applib.spring.init;

import org.marid.applib.spring.events.ContextStartedListener;
import org.marid.cache.MaridClassValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

public class InitBeanPostProcessor implements BeanPostProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(InitBeanPostProcessor.class);
  private static final MaridClassValue<Map<String, Integer>> METHOD_ORDERS = new MaridClassValue<>(c -> () -> {
    final var name = c.getSimpleName() + ".methods";

    try (final var inputStream = c.getResourceAsStream(name)) {

      if (inputStream == null) {
        LOGGER.warn("Unable to find {}", name);
        return Map.of();
      }

      try (final Scanner scanner = new Scanner(inputStream, UTF_8)) {
        return IntStream.range(0, Integer.MAX_VALUE)
            .takeWhile(i -> scanner.hasNextLine())
            .mapToObj(i -> Map.entry(scanner.nextLine(), i))
            .filter(e -> !e.getKey().isEmpty())
            .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
      }
    }
  });

  private final GenericApplicationContext context;

  public InitBeanPostProcessor(GenericApplicationContext context) {
    this.context = context;
  }

  @Override
  public Object postProcessAfterInitialization(@Nullable Object bean, String beanName) throws BeansException {
    if (bean == null) {
      return null;
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
