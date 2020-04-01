package org.marid.misc;

/*-
 * #%L
 * marid-util
 * %%
 * Copyright (C) 2012 - 2020 MARID software development group
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
