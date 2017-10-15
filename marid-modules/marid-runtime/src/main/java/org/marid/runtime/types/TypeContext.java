/*-
 * #%L
 * marid-runtime
 * %%
 * Copyright (C) 2012 - 2017 MARID software development group
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

package org.marid.runtime.types;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.function.Function;

public interface TypeContext {

    @Nonnull
    Type getWildcard();

    @Nonnull
    Type getBeanType(@Nonnull String name);

    @Nonnull
    Type resolve(@Nullable Type owner, @Nonnull Type type);

    @Nonnull
    String resolvePlaceholders(@Nonnull String value);

    @Nonnull
    Class<?> getRaw(@Nonnull Type type);

    boolean isAssignable(@Nonnull Type from, @Nonnull Type to);

    @Nonnull
    ClassLoader getClassLoader();

    @Nonnull
    Type getClassType(@Nonnull Class<?> type);

    @Nonnull
    Type getType(@Nonnull Class<?> type);

    <T> T evaluate(@Nonnull Function<TypeEvaluator, T> callback);
}