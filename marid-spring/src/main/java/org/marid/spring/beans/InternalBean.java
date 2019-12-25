package org.marid.spring.beans;

import org.marid.spring.annotation.PrototypeScoped;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.NoSuchElementException;

import static org.springframework.beans.factory.BeanFactoryUtils.beanNamesForTypeIncludingAncestors;

@PrototypeScoped
@Component("$internalBean")
public final class InternalBean<T> {

  public final T bean;

  @SuppressWarnings("unchecked")
  public InternalBean(InjectionPoint injectionPoint, GenericApplicationContext context) {
    final var beanFactory = context.getDefaultListableBeanFactory();
    final String name;
    final Type type;
    if (injectionPoint.getMethodParameter() != null) {
      final var param = injectionPoint.getMethodParameter();
      name = param.getParameterName();
      final var t = param.getGenericParameterType();
      if (t instanceof ParameterizedType) {
        final var parameterizedType = (ParameterizedType) t;
        final var typeArgs = parameterizedType.getActualTypeArguments();
        if (typeArgs.length == 1) {
          type = typeArgs[0];
        } else {
          throw new IllegalStateException("Invalid injection parameter: " + param.getParameter());
        }
      } else {
        throw new IllegalStateException("Invalid injection point parameter: " + param.getParameter());
      }
    } else if (injectionPoint.getField() != null) {
      final var t = injectionPoint.getField().getGenericType();
      name = injectionPoint.getField().getName();
      if (t instanceof ParameterizedType) {
        final var parameterizedType = (ParameterizedType) t;
        final var typeArgs = parameterizedType.getActualTypeArguments();
        if (typeArgs.length == 1) {
          type = typeArgs[0];
        } else {
          throw new IllegalStateException("Invalid injection field: " + injectionPoint.getField());
        }
      } else {
        throw new IllegalStateException("Invalid injection point field: " + injectionPoint.getField());
      }
    } else {
      throw new IllegalStateException("Invalid injection point: " + injectionPoint);
    }
    final var beanNames = beanNamesForTypeIncludingAncestors(beanFactory, ResolvableType.forType(type), true, true);
    if (beanNames.length == 1) {
      bean = (T) context.getBean(beanNames[0]);
    } else {
      final var beanName = Arrays.stream(beanNames)
          .filter(n -> n.equals(name))
          .findFirst()
          .orElseThrow(() -> new NoSuchElementException("No such bean with name: " + name));
      bean = (T) context.getBean(beanName);
    }
  }
}
