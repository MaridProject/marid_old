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

package org.marid.runtime.beans;

import org.marid.io.Xmls;
import org.marid.misc.Calls;
import org.w3c.dom.Element;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import static org.marid.io.Xmls.attribute;
import static org.marid.misc.Builder.build;

/**
 * @author Dmitry Ovchinnikov
 */
public class Bean extends BeanMethod {

    @Nonnull
    public final String name;

    @Nullable
    public final String factory;

    @Nonnull
    public final List<BeanMethod> initializers;

    @Nonnull
    public final List<Bean> children;

    public Bean() {
        this("runtime", Object.class.getName(), Calls.<Constructor<?>>call(Object.class::getConstructor));
    }

    public Bean(@Nonnull String name,
                @Nullable String factory,
                @Nonnull String signature,
                @Nonnull BeanMethodArg... args) {
        super(signature, args);
        this.name = name;
        this.factory = factory;
        this.initializers = new ArrayList<>();
        this.children = new ArrayList<>();
    }

    public Bean(@Nonnull String name,
                @Nullable String factory,
                @Nonnull Constructor<?> constructor,
                @Nonnull BeanMethodArg... args) {
        super(constructor, args);
        this.name = name;
        this.factory = factory;
        this.initializers = new ArrayList<>();
        this.children = new ArrayList<>();
    }

    public Bean(@Nonnull String name,
                @Nullable String factory,
                @Nonnull Method method,
                @Nonnull BeanMethodArg... args) {
        super(method, args);
        this.name = name;
        this.factory = factory;
        this.initializers = new ArrayList<>();
        this.children = new ArrayList<>();
    }

    public Bean(@Nonnull String name,
                @Nullable String factory,
                @Nonnull Field field,
                @Nonnull BeanMethodArg... args) {
        super(field, args);
        this.name = name;
        this.factory = factory;
        this.initializers = new ArrayList<>();
        this.children = new ArrayList<>();
    }

    public Bean(@Nonnull Element element) {
        super(element);
        this.name = attribute(element, "name").orElseThrow(NullPointerException::new);
        this.factory = attribute(element, "factory").orElse(null);
        this.initializers = Xmls.nodes(element, Element.class)
                .filter(e -> "initializer".equals(e.getTagName()))
                .map(BeanMethod::new)
                .collect(Collectors.toList());
        this.children = Xmls.nodes(element, Element.class)
                .filter(e -> "bean".equals(e.getTagName()))
                .map(Bean::new)
                .collect(Collectors.toList());
    }

    public Bean add(BeanMethod... initializers) {
        Collections.addAll(this.initializers, initializers);
        return this;
    }

    public void addInitializers(Collection<BeanMethod> initializers) {
        this.initializers.addAll(initializers);
    }

    public Bean add(Bean... beans) {
        Collections.addAll(children, beans);
        return this;
    }

    public void addChildren(Collection<Bean> beans) {
        children.addAll(beans);
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ Arrays.deepHashCode(new Object[]{name, factory, initializers});
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj == null || obj.getClass() != getClass() || !super.equals(obj)) {
            return false;
        } else {
            final Bean that = (Bean) obj;
            return Arrays.deepEquals(
                    new Object[]{this.name, this.factory, this.initializers},
                    new Object[]{that.name, that.factory, that.initializers}
            );
        }
    }

    public void writeTo(@Nonnull Element element) {
        super.writeTo(element);

        element.setAttribute("name", name);

        if (factory != null) {
            element.setAttribute("factory", factory);
        }

        for (final BeanMethod i : initializers) {
            i.writeTo(build(element.getOwnerDocument().createElement("initializer"), element::appendChild));
        }

        for (final Bean b : children) {
            b.writeTo(build(element.getOwnerDocument().createElement("bean"), element::appendChild));
        }
    }

    @Override
    public String toString() {
        return "Bean" + Arrays.deepToString(new Object[]{name, factory, signature, args, initializers});
    }
}
