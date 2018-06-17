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
package org.marid.spring.init;

import org.marid.spring.events.ContextStartedListener;
import org.springframework.asm.ClassReader;
import org.springframework.asm.ClassVisitor;
import org.springframework.asm.Label;
import org.springframework.asm.MethodVisitor;
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
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicStampedReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Arrays.binarySearch;
import static java.util.Comparator.comparingInt;
import static java.util.logging.Level.INFO;
import static org.marid.logging.Log.log;
import static org.springframework.asm.ClassReader.SKIP_FRAMES;
import static org.springframework.asm.SpringAsmInfo.ASM_VERSION;
import static org.springframework.util.ClassUtils.getClassFileName;

public class InitBeanPostProcessor implements BeanPostProcessor {

  private static final ClassValue<Method[]> INIT_METHODS = new ClassValue<>() {
    @Override
    protected Method[] computeValue(Class<?> type) {
      final Method[] methods = Stream.of(type.getMethods())
          .filter(m -> m.isAnnotationPresent(Init.class))
          .toArray(Method[]::new);
      if (methods.length == 0) {
        return null;
      }

      final String[] methodNames = Stream.of(methods).map(Method::getName).distinct().sorted().toArray(String[]::new);
      final int[] lines = new int[methodNames.length];
      Arrays.fill(lines, Integer.MAX_VALUE);

      Stream.of(methods)
          .map(Method::getDeclaringClass)
          .distinct()
          .forEach(declaringClass -> {
            try (final var is = type.getResourceAsStream(getClassFileName(declaringClass))) {
              final var classReader = new ClassReader(is);
              classReader.accept(new ClassVisitor(ASM_VERSION) {
                @Override
                public MethodVisitor visitMethod(int acc, String name, String desc, String signature, String[] xs) {
                  final int index = binarySearch(methodNames, name);
                  if (index < 0) {
                    return null;
                  }
                  return new MethodVisitor(ASM_VERSION) {
                    @Override
                    public void visitLineNumber(int line, Label start) {
                      if (line < lines[index]) {
                        lines[index] = line;
                      }
                    }
                  };
                }
              }, SKIP_FRAMES);
            } catch (IOException x) {
              throw new UncheckedIOException(x);
            }
          });

      final var methodInfo = IntStream.range(0, lines.length)
          .mapToObj(i -> new AtomicStampedReference<>(methodNames[i], lines[i]))
          .sorted(comparingInt(AtomicStampedReference::getStamp))
          .map(r -> r.getReference() + ":" + r.getStamp())
          .collect(Collectors.joining(", "));
      log(INFO, "{0} method order: {1}", type.getName(), methodInfo);

      Arrays.sort(methods, comparingInt(m -> lines[binarySearch(methodNames, m.getName())]));

      return methods;
    }
  };

  private final GenericApplicationContext context;

  public InitBeanPostProcessor(GenericApplicationContext context) {
    this.context = context;
  }

  @Override
  public Object postProcessAfterInitialization(@Nullable Object bean, String beanName) throws BeansException {
    if (bean == null) {
      return null;
    }

    final var methods = INIT_METHODS.get(bean.getClass());
    if (methods == null) {
      return bean;
    }

    try {
      if (bean.getClass().isAnnotationPresent(InitAfterStart.class)) {
        context.addApplicationListener(new ContextStartedListener() {
          @Override
          public void onApplicationEvent(@NonNull ContextStartedEvent event) {
            context.getApplicationListeners().remove(this);
            try {
              invoke(bean, context.getDefaultListableBeanFactory(), methods);
            } catch (RuntimeException x) {
              throw x;
            } catch (Exception x) {
              throw new IllegalStateException(x);
            }
          }
        });
      } else {
        invoke(bean, context.getDefaultListableBeanFactory(), methods);
      }
    } catch (Exception x) {
      throw new BeanInitializationException("Unable to initialize " + beanName, x);
    }
    return bean;
  }

  private void invoke(Object bean, DefaultListableBeanFactory beanFactory, Method[] methods) throws Exception {
    for (final var method : methods) {
      final var args = IntStream.range(0, method.getParameterCount())
          .mapToObj(i -> new DependencyDescriptor(new MethodParameter(method, i), true, true))
          .map(dd -> beanFactory.resolveDependency(dd, null))
          .toArray();
      method.invoke(bean, args);
    }
  }
}
