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

import org.marid.expression.generic.GetExpression;
import org.w3c.dom.Element;

import javax.annotation.Nonnull;

import static org.marid.io.Xmls.attribute;
import static org.marid.io.Xmls.create;
import static org.marid.io.Xmls.element;

public final class GetExpr extends Expr implements GetExpression {

    @Nonnull
    private final Expr target;

    @Nonnull
    private final String field;

    public GetExpr(@Nonnull Expr target, @Nonnull String field) {
        this.target = target;
        this.field = field;
    }

    public GetExpr(@Nonnull Element element) {
        super(element);
        this.target = element("target", element).map(Expr::of).orElseThrow(() -> new NullPointerException("target"));
        this.field = attribute(element, "field").orElseThrow(() -> new NullPointerException("field"));
    }

    @Override
    @Nonnull
    public Expr getTarget() {
        return target;
    }

    @Override
    @Nonnull
    public String getField() {
        return field;
    }

    @Override
    public void writeTo(@Nonnull Element element) {
        super.writeTo(element);
        create(element, "target", t -> create(t, target.getTag(), target::writeTo));
        element.setAttribute("field", field);
    }

    @Override
    public String toString() {
        return target + "." + field;
    }
}