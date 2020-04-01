/**
 * Advanced generic type processing library.
 * It contains some useful methods to:
 * <ul>
 *   <li>check whether a generic type is assignable from another</li>
 *   <li>infer a common generic type from the given generic types</li>
 *   <li>resolve a generic type by providing formal and actual type pairs</li>
 *   <li>get a type hierarchy (superclasses & interfaces) of the given generic type</li>
 * </ul>
 */
@CheckedFunctionalInterface(
  targetPackageName = "org.marid.types",
  interfacePrefix = "Reflective",
  checkedThrowableClasses = {ReflectiveOperationException.class},
  wrapperExceptionClass = IllegalStateException.class,
  functionalInterfaces = {
    Supplier.class
  }
)
package org.marid.types;
/*-
 * #%L
 * marid-types
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

import org.marid.processors.CheckedFunctionalInterface;

import java.util.function.Supplier;
