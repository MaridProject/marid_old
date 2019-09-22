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

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Comparator;

public class Var implements TypeVariable<GenericDeclaration>, Comparable<Var> {

  private final GenericDeclaration declaration;
  private final String name;

  public Var(GenericDeclaration declaration, String name) {
    this.declaration = declaration;
    this.name = name;
  }

  public Var(TypeVariable<?> var) {
    this(var.getGenericDeclaration(), var.getName());
  }

  public static Type convert(Type type) {
    return type instanceof TypeVariable<?> ? new Var((TypeVariable<?>) type) : type;
  }

  @Override
  public Type[] getBounds() {
    return Types.EMPTY_TYPES;
  }

  @Override
  public GenericDeclaration getGenericDeclaration() {
    return declaration;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public AnnotatedType[] getAnnotatedBounds() {
    return new AnnotatedType[0];
  }

  @Override
  public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
    return null;
  }

  @Override
  public Annotation[] getAnnotations() {
    return new Annotation[0];
  }

  @Override
  public Annotation[] getDeclaredAnnotations() {
    return new Annotation[0];
  }

  @Override
  public int hashCode() {
    return declaration.hashCode() ^ name.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return obj == this
        || obj instanceof Var && ((Var) obj).name.equals(name) && ((Var) obj).declaration.equals(declaration);
  }

  @Override
  public String toString() {
    final String d;
    if (declaration instanceof Class<?>) {
      d = ((Class<?>) declaration).getSimpleName();
    } else if (declaration instanceof Executable) {
      final var e = (Executable) declaration;
      d = e.getDeclaringClass().getSimpleName() + "." + e.getName() + "/" + e.getParameterCount();
    } else {
      d = declaration.toString();
    }
    return d + "<" + name + ">";
  }

  @Override
  public int compareTo(@NotNull Var o) {
    final Comparator<Var> c = Comparator
        .comparing(Var::getGenericDeclaration, Comparator.comparing(GenericDeclaration::toString))
        .thenComparing(Var::getName);
    return c.compare(this, o);
  }
}
