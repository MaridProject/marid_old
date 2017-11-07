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

package org.marid.expression.runtime;

import org.marid.expression.generic.ConstExpression;
import org.marid.runtime.context.BeanContext;
import org.w3c.dom.Element;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static org.marid.io.Xmls.attribute;

public final class ConstExpr extends ValueExpr implements ConstExpression {

	private final ConstantType type;

	public ConstExpr(@Nonnull ConstantType type, @Nonnull String value) {
		super(value);
		this.type = type;
	}

	ConstExpr(@Nonnull Element element) {
		super(element);
		type = attribute(element, "type").map(ConstantType::valueOf).orElseThrow(() -> new NullPointerException("type"));
	}

	@Override
	protected Object execute(@Nullable Object self, @Nonnull BeanContext context) {
		final String v = context.resolvePlaceholders(getValue()).trim();
		if (v.isEmpty()) {
			return null;
		} else {
			return getType().converter.apply(v);
		}
	}

	@Nonnull
	@Override
	public ConstantType getType() {
		return type;
	}
}
