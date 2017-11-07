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
import org.marid.misc.Casts;

import javax.annotation.Nonnull;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.*;

import static com.google.common.reflect.TypeToken.of;

public class GuavaTypeEvaluator implements TypeEvaluator {

	private final Set<TypeToken<?>> passed = new HashSet<>();
	private final Map<TypeToken<?>, List<TypeToken<?>>> typeMappings = new LinkedHashMap<>();

	@Nonnull
	@Override
	public GuavaTypeEvaluator where(Type formal, Type actual) {
		where(TypeToken.of(formal), TypeToken.of(actual));
		return this;
	}

	private void where(TypeToken<?> formal, TypeToken<?> actual) {
		if (!passed.add(formal)) {
			return;
		}
		if (formal.isArray() && actual.isArray()) {
			where(formal.getComponentType(), actual.getComponentType());
		} else if (formal.getType() instanceof TypeVariable<?>) {
			final TypeVariable<?> typeVariable = (TypeVariable<?>) formal.getType();
			for (final Type bound : typeVariable.getBounds()) {
				where(of(bound), actual);
			}
			typeMappings.computeIfAbsent(formal, k -> new ArrayList<>()).add(actual.wrap());
		} else if (formal.getType() instanceof ParameterizedType) {
			final Class<?> formalRaw = formal.getRawType();
			final Class<?> actualRaw = actual.getRawType();
			if (formalRaw.isAssignableFrom(actualRaw)) {
				final TypeToken<?> superType = actual.getSupertype(Casts.cast(formalRaw));
				final ParameterizedType actualParameterized = (ParameterizedType) superType.getType();
				final ParameterizedType formalParameterized = (ParameterizedType) formal.getType();
				final Type[] actualTypeArgs = actualParameterized.getActualTypeArguments();
				final Type[] formalTypeArgs = formalParameterized.getActualTypeArguments();
				for (int i = 0; i < actualTypeArgs.length; i++) {
					where(of(formalTypeArgs[i]), of(actualTypeArgs[i]));
				}
			}
		} else if (formal.getType() instanceof WildcardType) {
			final WildcardType wildcardType = (WildcardType) formal.getType();
			for (final Type bound : wildcardType.getUpperBounds()) {
				where(of(bound), actual);
			}
		}
	}

	@Nonnull
	@Override
	public Type resolve(Type type) {
		try {
			return typeMappings.entrySet().stream()
					.reduce(new TypeResolver(), this::where, (r1, r2) -> r2)
					.resolveType(type);
		} finally {
			typeMappings.clear();
			passed.clear();
		}
	}

	private TypeToken<?> commonAncestor(Map.Entry<TypeToken<?>, List<TypeToken<?>>> entry) {
		final Optional<? extends TypeToken<?>> token = entry.getValue().stream()
				.flatMap(t -> t.getTypes().stream())
				.filter(c -> entry.getValue().stream().allMatch(t -> t.isSubtypeOf(c)))
				.findFirst();
		return token.isPresent() ? token.get() : entry.getKey();
	}

	private TypeResolver where(TypeResolver resolver, Map.Entry<TypeToken<?>, List<TypeToken<?>>> entry) {
		return resolver.where(entry.getKey().getType(), commonAncestor(entry).getType());
	}
}
