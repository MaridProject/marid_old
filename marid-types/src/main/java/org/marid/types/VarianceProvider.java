package org.marid.types;

/*-
 * #%L
 * marid-types
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

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Executable;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.ServiceLoader;
import java.util.WeakHashMap;

public abstract class VarianceProvider {

  private static final WeakHashMap<ClassLoader, VarianceProvider[]> PROVIDERS = new WeakHashMap<>();

  public abstract boolean isCovariant(AnnotatedElement element);

  public static boolean checkCovariant(ClassLoader classLoader, AnnotatedElement element) {
    final VarianceProvider[] providers;
    synchronized (PROVIDERS) {
      providers = PROVIDERS.computeIfAbsent(classLoader, c -> ServiceLoader.load(VarianceProvider.class, c).stream()
          .map(ServiceLoader.Provider::get)
          .toArray(VarianceProvider[]::new)
      );
    }
    return Arrays.stream(providers).anyMatch(p -> p.isCovariant(element));
  }

  public static boolean checkCovariant(TypeVariable<?> variable) {
    if (variable.isAnnotationPresent(Covariant.class)) {
      return true;
    }
    final var decl = variable.getGenericDeclaration();
    if (decl.isAnnotationPresent(Covariant.class)) {
      return true;
    }
    if (decl instanceof Class<?>) {
      return checkCovariant(((Class<?>) decl).getClassLoader(), variable);
    } else if (decl instanceof Executable) {
      return checkCovariant(((Executable) decl).getDeclaringClass().getClassLoader(), variable);
    } else {
      throw new IllegalArgumentException("Unsupported generic declaration type: " + decl);
    }
  }
}
