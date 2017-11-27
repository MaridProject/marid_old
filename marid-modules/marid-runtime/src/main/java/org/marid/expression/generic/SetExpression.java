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

package org.marid.expression.generic;

import org.marid.annotation.MetaInfo;
import org.marid.beans.BeanTypeContext;
import org.marid.types.Types;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

@MetaInfo(name = "Field Setter", icon = "F_SEND")
public interface SetExpression extends Expression {

  @Nonnull
  Expression getTarget();

  @Nonnull
  String getField();

  @Nonnull
  Expression getValue();

  @Nonnull
  @Override
  default Type getType(@Nullable Type owner, @Nonnull BeanTypeContext context) {
    return getTarget().getType(owner, context);
  }

  @Override
  default void resolve(@Nonnull Type type, @Nonnull BeanTypeContext context, @Nonnull BiConsumer<Type, Type> evaluator) {
    if (getTarget() instanceof ThisExpression) {
      Types.rawClasses(type).flatMap(c -> Stream.of(c.getFields()))
          .filter(f -> f.getName().equals(getField()))
          .findFirst()
          .ifPresent(f -> evaluator.accept(context.resolve(type, f.getGenericType()), getValue().getType(type, context)));
    }
  }
}
