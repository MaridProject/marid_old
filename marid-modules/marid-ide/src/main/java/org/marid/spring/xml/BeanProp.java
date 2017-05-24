/*
 * Copyright (c) 2016 Dmitry Ovchinnikov
 * Marid, the free data acquisition and visualization software
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.marid.spring.xml;

import org.marid.jfx.beans.OOProp;
import org.marid.jfx.beans.OString;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Objects;

import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static org.marid.misc.Iterables.nodes;
import static org.marid.spring.xml.DElement.read;
import static org.marid.spring.xml.DElement.write;

/**
 * @author Dmitry Ovchinnikov
 */
public class BeanProp extends AbstractData implements DElementHolder {

    public final OString name = new OString("name");
    public final OOProp<DElement> data = new OOProp<>("data");

    public BeanProp() {
        name.addListener(this::fireInvalidate);
        data.addListener(this::fireInvalidate);
    }

    @Override
    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    @Override
    public DElement getData() {
        return data.get();
    }

    public void setData(DElement data) {
        this.data.set(data);
    }

    public boolean isEmpty() {
        return data.get() == null;
    }

    @Override
    public OString nameProperty() {
        return name;
    }

    @Override
    public OOProp<DElement> dataProperty() {
        return data;
    }

    @Override
    public String toString() {
        return String.format("Prop(%s,%s)", getName(), getData());
    }

    @Override
    public void loadFrom(Document document, Element element) {
        of(element.getAttribute("name")).filter(f -> !f.isEmpty()).ifPresent(name::set);
        nodes(element, Element.class).map(e -> read(document, e)).filter(Objects::nonNull).forEach(data::set);
    }

    @Override
    public void writeTo(Document document, Element element) {
        ofNullable(name.get()).filter(s -> !s.isEmpty()).ifPresent(e -> element.setAttribute("name", e));
        ofNullable(data.get()).filter(e -> !e.isEmpty()).ifPresent(e -> write(document, element, e));
    }
}
