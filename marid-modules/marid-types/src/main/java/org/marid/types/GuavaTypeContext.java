/*-
 * #%L
 * marid-ide
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

package org.marid.types;

import com.google.common.reflect.TypeResolver;
import com.google.common.reflect.TypeToken;
import org.marid.beans.TypedBean;
import org.marid.misc.Casts;
import org.springframework.util.TypeUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.function.Function;

import static org.marid.types.TypeUtils.WILDCARD;

public class GuavaTypeContext implements TypeContext {

	private final TypedBean bean;
	private final ClassLoader classLoader;

	public GuavaTypeContext(TypedBean bean, ClassLoader classLoader) {
		this.bean = bean;
		this.classLoader = classLoader;
	}

	@Nonnull
	@Override
	public Type getBeanType(@Nonnull String name) {
		return bean.matchingCandidates()
				.filter(b -> name.equals(b.getName()))
				.filter(TypedBean.class::isInstance)
				.map(TypedBean.class::cast)
				.findFirst()
				.map(b -> b.getFactory().resolveType(null, new GuavaTypeContext(b, classLoader)))
				.orElse(WILDCARD);
	}

	@Nonnull
	@Override
	public Type resolve(@Nullable Type owner, @Nonnull Type type) {
		return owner == null ? type : TypeToken.of(owner).resolveType(type).getType();
	}

	@Nonnull
	@Override
	public Class<?> getRaw(@Nonnull Type type) {
		return TypeToken.of(type).getRawType();
	}

	@Override
	public boolean isAssignable(@Nonnull Type from, @Nonnull Type to) {
		return TypeUtils.isAssignable(to, from);
	}

	@Nonnull
	@Override
	public ClassLoader getClassLoader() {
		return classLoader;
	}

	@Nonnull
	@Override
	public Type getClassType(@Nonnull Class<?> type) {
		final ParameterizedType parameterizedType = (ParameterizedType) getType(Class.class);
		return new TypeResolver()
				.where(parameterizedType.getActualTypeArguments()[0], type)
				.resolveType(parameterizedType);
	}

	@Nonnull
	@Override
	public Type getType(@Nonnull Class<?> type) {
		final TypeToken<?> t = TypeToken.of(type);
		final TypeToken<?> c = t.getSupertype(Casts.cast(type));
		return c.getType();
	}

	@Override
	public <T> T evaluate(@Nonnull Function<TypeEvaluator, T> callback) {
		return callback.apply(new GuavaTypeEvaluator());
	}
}
