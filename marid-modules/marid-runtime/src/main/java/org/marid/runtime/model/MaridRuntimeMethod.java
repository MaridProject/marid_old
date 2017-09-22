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
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.of;
import static org.marid.io.Xmls.attribute;
import static org.marid.io.Xmls.nodes;

public class MaridRuntimeMethod implements MaridMethod {

    private final MaridRuntimeBean parent;
    private final String signature;
    private final List<MaridRuntimeArgument> arguments;

    public MaridRuntimeMethod(@Nullable MaridRuntimeBean parent,
                              @Nonnull String signature,
                              @Nonnull Expression... arguments) {
        this.parent = parent;
        this.signature = signature;
        this.arguments = of(arguments).map(a -> new MaridRuntimeArgument(this, a)).collect(toList());
    }

    public MaridRuntimeMethod(@Nullable MaridRuntimeBean parent, @Nonnull Element element) {
        this.parent = parent;
        this.signature = attribute(element, "signature").orElseThrow(NullPointerException::new);
        this.arguments = nodes(element, Element.class)
                .filter(e -> "args".equals(e.getTagName()))
                .flatMap(e -> nodes(e, Element.class))
                .map(e -> new MaridRuntimeArgument(this, Expression.from(e)))
                .collect(Collectors.toList());
    }

    @Nullable
    @Override
    public MaridRuntimeBean getParent() {
        return parent;
    }

    @Nonnull
    @Override
    public String getSignature() {
        return signature;
    }

    @Nonnull
    @Override
    public List<MaridRuntimeArgument> getArguments() {
        return arguments;
    }

    public void writeTo(Element element) {
        element.setAttribute("signature", signature);

        final Document document = element.getOwnerDocument();
        final Element args = document.createElement("args");
        element.appendChild(args);

        for (final MaridRuntimeArgument arg : arguments) {
            final Element e = document.createElement(arg.getExpression().getTag());
            arg.getExpression().saveTo(e);
            args.appendChild(e);
        }
    }

    @Override
    public String toString() {
        return signature + arguments;
    }
}
