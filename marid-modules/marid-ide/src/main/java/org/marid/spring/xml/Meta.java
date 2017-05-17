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

import org.marid.jfx.beans.OString;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static java.util.Optional.of;
import static java.util.Optional.ofNullable;

/**
 * @author Dmitry Ovchinnikov
 */
public class Meta extends AbstractData<Meta> {

    public final OString key = new OString("key");
    public final OString value = new OString("value");

    public Meta() {
        this(null, null);
    }

    public Meta(String key, String value) {
        this.key.set(key);
        this.value.set(value);
        this.key.addListener(this::fireInvalidate);
        this.value.addListener(this::fireInvalidate);
    }

    @Override
    public void loadFrom(Document document, Element element) {
        of(element.getAttribute("key")).filter(s -> !s.isEmpty()).ifPresent(key::set);
        of(element.getAttribute("value")).filter(s -> !s.isEmpty()).ifPresent(value::set);
    }

    @Override
    public void writeTo(Document document, Element element) {
        ofNullable(key.get()).filter(s -> !s.isEmpty()).ifPresent(e -> element.setAttribute("key", e));
        ofNullable(value.get()).filter(s -> !s.isEmpty()).ifPresent(e -> element.setAttribute("value", e));
    }
}