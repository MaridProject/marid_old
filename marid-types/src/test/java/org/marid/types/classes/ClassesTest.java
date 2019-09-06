package org.marid.types.classes;

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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.marid.types.ClassStreams;
import org.marid.types.Classes;
import org.marid.types.classes.ClassStreamsTest.C1;
import org.marid.types.classes.ClassStreamsTest.C4;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("normal")
class ClassesTest {

  @Test
  void isPublic() {
    Assertions.assertEquals(
        List.of(C1.class, C4.class, Object.class),
        ClassStreams.superclasses(C1.class)
            .filter(Classes::isPublic)
            .collect(Collectors.toList())
    );
  }

  @Test
  void methods() {
    assertEquals(
        Set.of("m1", "m2", "m3", "im3", "im1"),
        Classes.publicMethods(C1.class)
            .filter(m -> m.getDeclaringClass() != Object.class)
            .map(Method::getName)
            .collect(Collectors.toSet())
    );
  }
}
