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

package org.marid.runtime.model;

import org.marid.runtime.expression.Expression;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ModelFactory<B extends MaridBean, M extends MaridMethod, A extends MaridArgument> {

    @Nonnull
    B bean(@Nullable B parent, @Nonnull String name, @Nullable String factory, @Nonnull String signature, @Nonnull Expression... arguments);
}
