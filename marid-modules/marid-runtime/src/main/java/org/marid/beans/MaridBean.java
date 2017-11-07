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

package org.marid.beans;

import org.marid.expression.generic.Expression;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Stream.*;

public interface MaridBean {

	MaridBean getParent();

	@Nonnull
	String getName();

	@Nonnull
	Expression getFactory();

	@Nonnull
	List<? extends MaridBean> getChildren();

	@Nonnull
	default Stream<? extends MaridBean> ancestors() {
		return ofNullable(getParent()).flatMap(p -> concat(of(p), p.ancestors()));
	}

	@Nonnull
	default Stream<? extends MaridBean> descendants() {
		return getChildren().stream().flatMap(b -> concat(of(b), b.descendants()));
	}

	@Nonnull
	default Stream<? extends MaridBean> siblings() {
		return ofNullable(getParent()).flatMap(p -> p.getChildren().stream().filter(c -> c != this));
	}

	@Nonnull
	default Stream<? extends MaridBean> matchingCandidates() {
		return concat(siblings(), ancestors()
				.filter(p -> p.getParent() != null)
				.flatMap(p -> concat(of(p), p.siblings())));
	}
}
