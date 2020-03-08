package org.marid.misc;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Optional;
import java.util.WeakHashMap;
import java.util.stream.Stream;

public class Annotations {

  private static final WeakHashMap<Object, Annotation> PROXIES = new WeakHashMap<>();

  public static Stream<Annotation> fetch(AnnotatedElement element) {
    final var ccl = Thread.currentThread().getContextClassLoader();
    return Arrays.stream(element.getAnnotations())
      .map(ra -> {
        final Class<? extends Annotation> ca;
        try {
          ca = ccl.loadClass(ra.annotationType().getName()).asSubclass(Annotation.class);
        } catch (ClassNotFoundException e) {
          throw new IllegalStateException(e);
        }
        if (ca == ra.annotationType()) {
          return ra;
        } else {
          final var proxy = ca.cast(Proxy.newProxyInstance(ccl, new Class<?>[]{ca}, (p, m, args) -> {
            switch (m.getName()) {
              case "annotationType":
                return ra.annotationType();
              case "equals": {
                final Annotation pa;
                synchronized (PROXIES) {
                  pa = PROXIES.get(args[0]);
                }
                return pa == null ? ra.equals(args[0]) : ra.equals(pa);
              }
              case "hashCode":
                return ra.hashCode();
              case "toString":
                return ra.toString();
              default: {
                final var method = ra.annotationType().getMethod(m.getName(), m.getParameterTypes());
                return method.invoke(ra, args);
              }
            }
          }));
          synchronized (PROXIES) {
            PROXIES.put(proxy, ra);
          }
          return proxy;
        }
      });
  }

  public static <A extends Annotation> Stream<A> fetch(AnnotatedElement element, Class<A> type) {
    return fetch(element).filter(type::isInstance).map(type::cast);
  }

  public static <A extends Annotation> Optional<A> fetchOne(AnnotatedElement element, Class<A> type) {
    return fetch(element, type).findFirst();
  }
}
