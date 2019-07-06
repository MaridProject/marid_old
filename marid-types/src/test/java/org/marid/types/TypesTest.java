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

package org.marid.types;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.marid.types.AuxTypeUtils.*;
import org.marid.types.util.MappedVars;

import javax.print.attribute.Attribute;
import javax.print.attribute.IntegerSyntax;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.standard.JobKOctetsProcessed;
import java.io.Serializable;
import java.io.Writer;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.marid.types.AuxTypeUtils.*;
import static org.marid.types.Classes.classes;

@Tag("normal")
class TypesTest {

  @Test
  void resolveVars1() {
    final MappedVars map = Types.resolveVars(AuxTypeUtils.List2.class);

    final Set<Class<?>> ec = classes(AuxTypeUtils.List2.class).filter(c -> c.getTypeParameters().length > 0).collect(toSet());
    final Set<Class<?>> ac = map.vars().map(v -> (Class<?>) v.getGenericDeclaration()).collect(toSet());
    final Set<TypeVariable<?>> expectedVars = ec.stream().flatMap(c -> Stream.of(c.getTypeParameters())).collect(toSet());

    assertEquals(ec, ac);
    assertEquals(expectedVars, map.vars().collect(toSet()));
    assertEquals(Set.of(Writer.class), map.types().collect(toSet()));
  }

  @Test
  void resolveVars2() {
    final MappedVars map = Types.resolveVars(AuxTypeUtils.List2.M.class);

    final Set<Class<?>> ec = classes(AuxTypeUtils.List1.M.class).filter(c -> c.getTypeParameters().length > 0).collect(toSet());
    final Set<Class<?>> ac = map.vars().map(v -> (Class<?>) v.getGenericDeclaration()).collect(toSet());
    final Set<TypeVariable<?>> expectedVars = ec.stream().flatMap(c -> Stream.of(c.getTypeParameters())).collect(toSet());

    assertEquals(ec, ac);
    assertEquals(expectedVars, map.vars().collect(toSet()));
  }

  @Test
  void resolveVarsStackOverflow() {
    final MappedVars map = Types.resolveVars(AuxTypeUtils.Map2.class);

    final var expectedVar = Map1.class.getTypeParameters()[0];
    final var actualVar = map.vars().findFirst().orElseThrow();
    assertEquals(expectedVar, actualVar);

    final var expectedType = new MaridParameterizedType(AuxTypeUtils.class, I1.class, I1.class);
    final var actualType = map.types().findFirst().orElseThrow();
    assertEquals(expectedType, actualType);
  }

  private static Object[][] typesData() {
    return new Object[][]{
        {p(ArrayList.class, Integer.class), Arrays.asList(
            p(ArrayList.class, Integer.class),
            p(AbstractList.class, Integer.class),
            p(AbstractCollection.class, Integer.class),
            Object.class,
            p(List.class, Integer.class),
            p(Collection.class, Integer.class),
            p(Iterable.class, Integer.class),
            RandomAccess.class,
            Cloneable.class,
            Serializable.class
        )},
        {p(HashMap.class, Integer.class, w(Long.class)), Arrays.asList(
            p(HashMap.class, Integer.class, w(Long.class)),
            p(AbstractMap.class, Integer.class, w(Long.class)),
            Object.class,
            p(Map.class, Integer.class, w(Long.class)),
            Cloneable.class,
            Serializable.class
        )},
        {int[].class, Arrays.asList(int[].class, Object.class, Cloneable.class, Serializable.class)},
        {Integer[].class, Arrays.asList(
            Integer[].class,
            Number[].class,
            Object[].class,
            a(p(Comparable.class, Integer.class)),
            Serializable[].class,
            Object.class,
            Cloneable.class,
            Serializable.class
        )},
        {a(p(ArrayList.class, Long.class)), Arrays.asList(
            a(p(ArrayList.class, Long.class)),
            a(p(AbstractList.class, Long.class)),
            a(p(AbstractCollection.class, Long.class)),
            Object[].class,
            a(p(List.class, Long.class)),
            a(p(Collection.class, Long.class)),
            a(p(Iterable.class, Long.class)),
            RandomAccess[].class,
            Cloneable[].class,
            Serializable[].class,
            Object.class,
            Cloneable.class,
            Serializable.class
        )}
    };
  }

  @ParameterizedTest
  @MethodSource("typesData")
  void types(Type type, List<Type> expected) {
    final List<Type> actual = Types.typesTree(type).collect(toList());
    assertEquals(expected, actual);
  }

  private static Object[][] assignmentsData() {
    return new Object[][]{
        {Object.class, int.class, true},
        {long.class, int.class, false},
        {void.class, int.class, false},
        {void.class, void.class, true},
        {p(Map.class, Integer.class, Number.class), p(Map.class, Integer.class, Number.class), true},
        {p(Map.class, Integer.class, Number.class), p(Map.class, Integer.class, BigInteger.class), true},
        {p(Map.class, Integer.class, Number.class), p(HashMap.class, Integer.class, BigInteger.class), true},
        {p(Map.class, Object.class, Object.class), Properties.class, true}
    };
  }

  @ParameterizedTest
  @MethodSource("assignmentsData")
  void assignments(Type to, Type from, boolean expected) {
    final boolean actual = Types.isAssignable(to, from);
    assertEquals(expected, actual);
  }

  @Test
  void typesTreeInterfaces() {
    final Set<Type> types = Types.typesTree(JobKOctetsProcessed.class).collect(toCollection(LinkedHashSet::new));
    assertEquals(
        Set.of(
            JobKOctetsProcessed.class,
            IntegerSyntax.class,
            Object.class,
            PrintJobAttribute.class,
            Attribute.class,
            Serializable.class,
            Cloneable.class
        ), types);
  }
}
